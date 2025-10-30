package gt.eg.ldapvalidatorservice.hr;

public record Employee(
        String codigoColaborador,
        String nombreColaborador,
        String puestoColaborador,
        String departamentoColaborador,
        String subGerencia,
        String gerencia1,
        String gerencia2,
        String direccion,
        String compania,
        String noTelAutoconsumo,
        String correoElectronico,
        String dpi,
        String sede,
        String codigoJefeColaborador,
        String nombreJefeColaborador,
        String puestoJefeColaborador,
        String gerenciaJefeColaborador,
        String correoElectronicoJefe,
        String numeroTelJefe,
        String devuelve
) {}
