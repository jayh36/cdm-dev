package com.skcc.spring.service;

import com.skcc.spring.model.Board;
import com.skcc.spring.model.User;
import com.skcc.spring.repository.BoardRepository;
import com.skcc.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {
    @Autowired
    BoardRepository boardRepository;

    @Autowired
    UserRepository userRepository;

    public Board save(String username, Board board){
        User user = userRepository.findByUsername(username);
        board.setUser(user);
        return boardRepository.save(board);
    }
}
