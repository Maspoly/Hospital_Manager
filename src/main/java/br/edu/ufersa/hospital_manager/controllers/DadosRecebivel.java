package br.edu.ufersa.hospital_manager.controllers;

/**
 * Interface para controllers que podem receber dados via NavigationHelper
 */
public interface DadosRecebivel {
    void receberDados(String key, Object value);
}