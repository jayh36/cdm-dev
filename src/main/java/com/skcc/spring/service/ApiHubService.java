package com.skcc.spring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skcc.spring.util.CryptoNoHexUtil;
import com.skcc.spring.util.CryptoUtil;
import com.skcc.spring.util.HttpUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class ApiHubService {

    private static ObjectMapper mapper = new ObjectMapper();	// lib 경로의 jackson-databind, jackson-core, jackson-annotations jar파일

    private static final String basic = "https://apihubs.sktelecom.com/api";
    private static final String path = "/is/ISSWG00078/"; // or /im/제공API서비스ID/ (mashup)
    private static final String apiKey = "889om9j6j5s01nv9";// "SKT에서 제공받은 API_KEY";
    private static final String decKey = "wgqaue5g1urk9vyx";// "SKT에서 제공받은 복호화 KEY";
    //API Hub 표준 응답 방식은 JSON : 변경 필요시 운영자 협의필요
    private static final String returnType = "JSON";

    // API Hub 담당자가 암호화된 데이터를 Hex 미사용 되었다고 한다면 해당 값을 N 으로 변경 필요 ( !!!! 중요 !!!! )
    private static final String decHexYn = "Y";

    public Map<String, Object> callApiHub(String input){

        System.setProperty("javax.net.debug", "ssl");

        /**
         * JAVA 가 구버젼 일 경우 ssl연결 시 인증서 관련 에러가 발생 할 수 있으며, 해당 사항의 경우는 installCert.java 파일을 이용하는 방법 등을 이용하여
         * 인증서를 내부 서버에 업데이트 해야합니다. ( InstallCert.java 파일 상단에  java의 인증서가 설치되는 경로를 확인해 주세요. )
         * javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed:
         * 	sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
         * 실행 방법 : java InstallCert apihub.sktelecom.com
         */
        System.err.println("///////////////////  API HUB TEST - START  ///////////////////");

        // 주소
        String url = basic + path + apiKey;
        System.err.println("Request URL : "+url);

        //returnType default는 JSON, JSON응답을 원하는 경우는 넣지 않아도 되며, XML을 원할 경우 Value값에 XML로 표기하여 추가 필요.
        //url += "?returnType="+returnType;

        // request parameter
        String param = "";
        Map<String, Object> dataMap = null;
        Map<String, Object> data = null;
        Map<String, Object> header = null;
        Map<String, Object> body = null;

        try {
            Map<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("SVC_MGMT_NUM", input);
            //requestParam.put("KEY02", "DATA02");

            param = mapper.writeValueAsString(requestParam);
            System.err.println("Request Param : "+param);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpUtil http = new HttpUtil(url, param);
        String resp = null;
        try {
            resp = http.httpConnection();
            /**
             * JSON : {"RESPONSE":{"HEADER":{"RESULT_CODE":"00","RESULT":"S","RESULT_MESSAGE":"서비스가 정상적으로 처리되었습니다."},"BODY":"암호화된 String 내용"}}
             * XML  : <?xml version="1.0" encoding="UTF-8" standalone="yes"?><RESPONSE><HEADER><RESULT_CODE>00</RESULT_CODE><RESULT>S</RESULT><RESULT_MESSAGE>서비스가 정상적으로 처리되었습니다.</RESULT_MESSAGE></HEADER><BODY><KEY>암호화된 String내용</KEY></BODY></RESPONSE>
             */
            System.err.println("Response : "+resp);

            if( resp != null ) {

                if( "JSON".equalsIgnoreCase(returnType) ) {
                    dataMap = mapper.readValue(resp, Map.class);
                    data = (Map<String, Object>) dataMap.get("RESPONSE");
                } else {
                    // XML
                    JSONObject jobj = XML.toJSONObject(resp);
                    dataMap = mapper.readValue(jobj.toString(), Map.class);
                    data = (Map<String, Object>) dataMap.get("RESPONSE");
                }

                if( data != null) {
                    header = (Map<String, Object>) data.get("HEADER");

                    String result = header.get("RESULT").toString();
                    if( "S".equals(result) ) {
                        System.err.println("Result : Success.");
                        System.err.println("Message : " + header.get("RESULT_MESSAGE"));

                        // Body 추출
                        if( "JSON".equalsIgnoreCase(returnType) ) {
                            String encBody = data.get("BODY").toString();
                            String strBody = decrypt(encBody);
                            body = mapper.readValue(strBody, Map.class);
                            System.err.println("decBody : " + body);
                        } else {
                            // XML
                            Map<String, String> encBodyMap = (Map<String, String>) data.get("BODY");
                            //body = new HashMap<String, Object>();
                            for( String key : encBodyMap.keySet() ){
                                body.put(key, decrypt(encBodyMap.get(key)));
                            }
                        }

                    } else {
                        System.err.println("Result : Failed.");
                        System.err.println("Message : " + header.get("RESULT_MESSAGE"));
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        System.err.println("///////////////////  API HUB TEST - END  ///////////////////");
        return body;
    }

    private static String decrypt(String data) throws Exception {
        // !!중요!!
        if( "Y".equals(decHexYn) ) {
            // Hex 사용 복호화 사용시
            byte[] decodeBase64 = Base64.decodeBase64(data);
            return CryptoUtil.decrypt(new String(decodeBase64, "UTF-8"), decKey);
        } else {
            // Hex 미 사용 복호화 사용시
            return CryptoNoHexUtil.decrypt(data, decKey);
        }
    }
}
