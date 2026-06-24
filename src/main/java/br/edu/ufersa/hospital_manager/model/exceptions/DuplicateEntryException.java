package br.edu.ufersa.hospital_manager.model.exceptions;

/**
 * Lançada quando se tenta cadastrar uma entidade cujo campo único
 * (CPF, código de conselho, etc.) já existe no banco de dados.
 *
 * <p>Exemplos de uso:</p>
 * <ul>
 *   <li>Cadastro de paciente com CPF já registrado</li>
 *   <li>Cadastro de médico com CPF ou CRM já registrado</li>
 *   <li>Cadastro de gerente com CPF já registrado</li>
 * </ul>
 *
 * <p>É uma exceção de regra de negócio — distinta de {@link java.sql.SQLException},
 * que indica falha técnica no banco, e de {@link EntityNotFoundException},
 * que indica ausência de registro.</p>
 */
public class DuplicateEntryException extends RuntimeException {

    private final String field;
    private final String value;

    /**
     * @param field nome do campo duplicado (ex.: "CPF", "Código de Conselho")
     * @param value valor que causou a duplicidade
     */
    public DuplicateEntryException(String field, String value) {
        super(field + " já cadastrado: " + value);
        this.field = field;
        this.value = value;
    }

    /**
     * Construtor com mensagem personalizada.
     *
     * @param message mensagem de erro direta para exibição ao usuário
     */
    public DuplicateEntryException(String message) {
        super(message);
        this.field = null;
        this.value = null;
    }

    /** @return nome do campo que gerou a duplicidade, ou {@code null} se não informado */
    public String getField() {
        return field;
    }

    /** @return valor duplicado, ou {@code null} se não informado */
    public String getValue() {
        return value;
    }
}
