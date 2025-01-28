package modelBD;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "prestamos")
public class PrestamosBD {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long id_cliente;
    private Long id_asesor; // Asesor responsable del préstamo y del cobro
    private double monto_prestado;
    private double monto_restante;
    private double abono;
    private Date fecha_prestamo;
    private boolean activo;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Long id_cliente) {
        this.id_cliente = id_cliente;
    }

    public Long getId_asesor() {
        return id_asesor;
    }

    public void setId_asesor(Long id_asesor) {
        this.id_asesor = id_asesor;
    }

    public double getMonto_prestado() {
        return monto_prestado;
    }

    public void setMonto_prestado(double monto_prestado) {
        this.monto_prestado = monto_prestado;
    }

    public double getMonto_restante() {
        return monto_restante;
    }

    public void setMonto_restante(double monto_restante) {
        this.monto_restante = monto_restante;
    }

    public double getAbono() {
        return abono;
    }

    public void setAbono(double abono) {
        this.abono = abono;
    }

    public Date getFecha_prestamo() {
        return fecha_prestamo;
    }

    public void setFecha_prestamo(Date fecha_prestamo) {
        this.fecha_prestamo = fecha_prestamo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
