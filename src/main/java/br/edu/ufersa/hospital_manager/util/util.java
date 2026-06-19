package br.edu.ufersa.hospital_manager.util;

public class util {
    
}
    // ─────────────────────────────────────────────────────────
    // Utilitários de navegação
    // ─────────────────────────────────────────────────────────

    /**
     * Marca o botão selecionado como ativo e remove o estilo dos demais.
     */
    private void setNavAtivo(Button botaoAtivo) {
        Button[] todos = {
            btnDashboard, btnMedicos, btnPacientes,
            btnConsultas, btnBusca, btnRelatorios
        };
        for (Button btn : todos) {
            btn.getStyleClass().remove("nav-btn-active");
        }
        if (!botaoAtivo.getStyleClass().contains("nav-btn-active")) {
            botaoAtivo.getStyleClass().add("nav-btn-active");
        }
    }

    /**
     * Carrega outro FXML na área central.
     * Adapte conforme a arquitetura de navegação do seu projeto
     * (ex.: injetar um controlador-raiz, usar um ScreenManager, etc.).
     *
     * @param fxmlPath caminho relativo ao classpath do arquivo FXML
     */
    private void navegarPara(String fxmlPath) {
        try {
            // Exemplo de navegação com troca de cena:
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.getScene().setRoot(root);

            System.out.println("Navegando para: " + fxmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }