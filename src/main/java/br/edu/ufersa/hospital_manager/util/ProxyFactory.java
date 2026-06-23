package br.edu.ufersa.hospital_manager.util;

import br.edu.ufersa.hospital_manager.model.services.AddressService;
import br.edu.ufersa.hospital_manager.model.services.AddressServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ConsultationService;
import br.edu.ufersa.hospital_manager.model.services.ConsultationServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.DoctorService;
import br.edu.ufersa.hospital_manager.model.services.DoctorServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.IsServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.LoginService;
import br.edu.ufersa.hospital_manager.model.services.LoginServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ManagerService;
import br.edu.ufersa.hospital_manager.model.services.ManagerServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.MedicalRecordService;
import br.edu.ufersa.hospital_manager.model.services.MedicalRecordServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.PatientService;
import br.edu.ufersa.hospital_manager.model.services.PatientServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ReportService;
import br.edu.ufersa.hospital_manager.model.services.ReportServiceProxy;

public class ProxyFactory {
    public static IsServiceProxy createProxy(String serviceName) {
        if (serviceName.equals("LOGIN")) {
            return new LoginServiceProxy(new LoginService());
        } else if (serviceName.equals("DOCTOR")) {
            return new DoctorServiceProxy(new DoctorService());
        } else if (serviceName.equals("MANAGER")) {
            return new ManagerServiceProxy(new ManagerService());
        } else if (serviceName.equals("MEDICAL_RECORD")) {
            return new MedicalRecordServiceProxy(new MedicalRecordService());
        } else if (serviceName.equals("PATIENT")) {
            return new PatientServiceProxy(new PatientService());
        } else if (serviceName.equals("CONSULTATION")) {
            return new ConsultationServiceProxy(new ConsultationService());
        } else if (serviceName.equals("REPORT")) {
            return new ReportServiceProxy(new ReportService());
        } else if (serviceName.equals("ADDRESS")) {
            return new AddressServiceProxy(new AddressService());
        }
        else{
            throw new IllegalArgumentException("Unknown service: " + serviceName);
        }
    }
}
