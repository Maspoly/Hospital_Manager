package br.edu.ufersa.hospital_manager.model.exceptions;

/**
 * Lançada quando uma entidade requisitada não existe no banco de dados.
 *
 * <p>Exemplos de uso:</p>
 * <ul>
 *   <li>Tentativa de atualizar ou remover um paciente que não existe</li>
 *   <li>Tentativa de cancelar uma consulta que não existe</li>
 *   <li>Tentativa de editar um prontuário já excluído</li>
 *   <li>Busca por ID, CPF ou nome que não retorna resultado</li>
 * </ul>
 *
 * <p>Diferencia-se de {@link java.sql.SQLException} (falha técnica de banco)
 * e de {@link DuplicateEntryException} (violação de unicidade).</p>
 */
public class EntityNotFoundException extends RuntimeException {

    private final String entityType;
    private final String identifier;

    /**
     * @param entityType tipo da entidade não encontrada (ex.: "Paciente", "Consulta")
     * @param identifier identificador usado na busca (ex.: o ID ou CPF informado)
     */
    public EntityNotFoundException(String entityType, String identifier) {
        super(entityType + " não encontrado(a): " + identifier);
        this.entityType = entityType;
        this.identifier = identifier;
    }

    /**
     * Construtor com mensagem personalizada.
     *
     * @param message mensagem de erro direta para exibição ao usuário
     */
    public EntityNotFoundException(String message) {
        super(message);
        this.entityType = null;
        this.identifier = null;
    }

    /** @return tipo da entidade não encontrada, ou {@code null} se não informado */
    public String getEntityType() {
        return entityType;
    }

    /** @return identificador utilizado na busca, ou {@code null} se não informado */
    public String getIdentifier() {
        return identifier;
    }
}
