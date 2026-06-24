package br.edu.ufersa.hospital_manager.model.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import br.edu.ufersa.hospital_manager.model.entities.Doctor;

/**
 * Lançada quando há conflito de horário ao tentar agendar uma consulta.
 *
 * <p>Diferente de {@link DuplicateEntryException} (que trata duplicidade de campos
 * únicos como CPF), esta exceção representa um conflito temporal entre entidades
 * relacionadas — o médico já possui uma consulta agendada no mesmo horário.</p>
 *
 * <p>O controller pode usar {@link #getDoctor()} e {@link #getConflictingDateTime()}
 * para sugerir horários alternativos ou exibir a agenda do médico ao usuário.</p>
 *
 * <p>Exemplos de uso:</p>
 * <ul>
 *   <li>Agendamento de consulta em horário já ocupado pelo médico</li>
 *   <li>Reagendamento que colide com outro horário existente</li>
 * </ul>
 */
public class SchedulingConflictException extends RuntimeException {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private final Doctor doctor;
    private final LocalDateTime conflictingDateTime;

    /**
     * @param doctor              médico com conflito de agenda
     * @param conflictingDateTime data e hora do conflito
     */
    public SchedulingConflictException(Doctor doctor, LocalDateTime conflictingDateTime) {
        super(buildMessage(doctor, conflictingDateTime));
        this.doctor = doctor;
        this.conflictingDateTime = conflictingDateTime;
    }

    /**
     * Construtor com mensagem personalizada.
     *
     * @param message mensagem de erro direta para exibição ao usuário
     */
    public SchedulingConflictException(String message) {
        super(message);
        this.doctor = null;
        this.conflictingDateTime = null;
    }

    private static String buildMessage(Doctor doctor, LocalDateTime dateTime) {
        String nomeMedico = doctor != null ? "Dr(a). " + doctor.getName() : "O médico";
        String horario = dateTime != null ? dateTime.format(FORMATTER) : "no horário solicitado";
        return nomeMedico + " já possui uma consulta agendada " + horario
                + ". Por favor, escolha outro horário.";
    }

    /** @return médico com conflito de agenda, ou {@code null} se não informado */
    public Doctor getDoctor() {
        return doctor;
    }

    /** @return data e hora do conflito, ou {@code null} se não informada */
    public LocalDateTime getConflictingDateTime() {
        return conflictingDateTime;
    }
}
