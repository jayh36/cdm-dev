package com.skcc.spring.validator;

import com.skcc.spring.model.Board;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import static org.springframework.util.StringUtils.hasLength;

@Component
public class BoardValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Board.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Board board = (Board) target;
//        if(board.getContent()==null){
//            errors.rejectValue("content", "key", "내용을 입력하세요.");
//        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", "empty", "내용을 입력해 주세요.");
    }
}
