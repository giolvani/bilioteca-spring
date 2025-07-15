package br.edu.ifpr.biblioteca_spring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model, HttpServletRequest request) {
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("error", "Dados inválidos");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("path", request.getRequestURI());
        return "error/error";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NoHandlerFoundException ex, Model model, HttpServletRequest request) {
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", "Página não encontrada");
        model.addAttribute("message", "A página solicitada não foi encontrada.");
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("path", request.getRequestURI());
        return "error/404";
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException ex, Model model, HttpServletRequest request) {
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", "Erro interno do servidor");
        model.addAttribute("message", "Ocorreu um erro inesperado. Tente novamente mais tarde.");
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("path", request.getRequestURI());
        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model, HttpServletRequest request) {
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", "Erro inesperado");
        model.addAttribute("message", "Ocorreu um erro inesperado. Nossa equipe foi notificada.");
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("path", request.getRequestURI());
        return "error/error";
    }
}
