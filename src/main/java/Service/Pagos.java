package Service;

import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ModelBD.PagosBD;
import ModelBD.PrestamosBD;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Path("Pagos")
public class Pagos {
    private static final SessionFactory sessionFactory;

    static {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @POST
@Path("guardar")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public Response guardar(@FormParam("id_prestamo") Long id_prestamo,
                        @FormParam("id_asesor") Long id_asesor,
                        @FormParam("id_cliente") Long id_cliente) {

    if (id_prestamo == null || id_prestamo <= 0) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El campo 'id_prestamo' es obligatorio y debe ser mayor a cero.\"}")
                .build();
    }
    if (id_asesor == null || id_asesor <= 0) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El campo 'id_asesor' es obligatorio y debe ser mayor a cero.\"}")
                .build();
    }
    if (id_cliente == null || id_cliente <= 0) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El campo 'id_cliente' es obligatorio y debe ser mayor a cero.\"}")
                .build();
    }

    Transaction transaction = null;

    try (Session session = sessionFactory.openSession()) {
        transaction = session.beginTransaction();

        PrestamosBD prestamo = session.get(PrestamosBD.class, id_prestamo);
        if (prestamo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No se encontró el préstamo con ID " + id_prestamo + ".\"}")
                    .build();
        }

        double tasa_interes = prestamo.getTasa_interes(); 
        double monto_prestamo = prestamo.getMonto_prestado(); 
        int plazo = prestamo.getPlazo(); 

        double tasa_mensual = (tasa_interes / 12) / 100; 

        
        double cuota = (monto_prestamo * (tasa_mensual * Math.pow(1 + tasa_mensual, plazo))) / 
                       (Math.pow(1 + tasa_mensual, plazo) - 1);

        cuota = Math.round(cuota * 100.0) / 100.0; // Redondear a 2 decimales

        PagosBD pagos = new PagosBD();
        pagos.setId_prestamo(id_prestamo);
        pagos.setId_asesor(id_asesor);
        pagos.setId_cliente(id_cliente);
        pagos.setCuota(cuota); 
        pagos.setMonto_restante(prestamo.getMonto_prestado());
        pagos.setFecha_pago(new Date());
        pagos.setActivo(true);

        session.save(pagos);
        transaction.commit();

        return Response.ok("{\"message\":\"El pago se ha guardado correctamente.\", \"cuota\": " + cuota + "}").build();
    } catch (Exception e) {
        if (transaction != null) {
            transaction.rollback();
        }
        e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"No se logró guardar el pago: " + e.getMessage() + "\"}")
                .build();
    }
}
    @POST
@Path("abonar")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public Response abonar(@FormParam("id_prestamo") Long id_prestamo,
                       @FormParam("abono") double abono) {

    if (id_prestamo == null || id_prestamo <= 0) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El campo 'id_prestamo' es obligatorio y debe ser mayor a cero.\"}")
                .build();
    }
    if (abono <= 0) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"El monto del abono debe ser mayor a cero.\"}")
                .build();
    }

    Transaction transaction = null;

    try (Session session = sessionFactory.openSession()) {
        transaction = session.beginTransaction();

       
        PrestamosBD prestamo = session.get(PrestamosBD.class, id_prestamo);
        if (prestamo == null || !prestamo.isActivo()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Préstamo no encontrado o está inactivo.\"}")
                    .build();
        }

        // Obtener el último pago registrado para este préstamo
        PagosBD pagos = (PagosBD) session.createQuery("FROM PagosBD WHERE id_prestamo = :id_prestamo ORDER BY id DESC")
                .setParameter("id_prestamo", id_prestamo)
                .setMaxResults(1)
                .uniqueResult();

        if (pagos == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No se encontró un pago registrado para este préstamo.\"}")
                    .build();
        }

        
        double nuevoMontoRestante = pagos.getMonto_restante() - abono;
        if (nuevoMontoRestante < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El abono no puede ser mayor al monto restante.\"}")
                    .build();
        }

        
        pagos.setMonto_restante(nuevoMontoRestante);
        pagos.setAbono(abono);
        pagos.setFecha_pago(new Date()); 

        session.update(pagos);
        transaction.commit();

        return Response.ok("{\"message\":\"Abono registrado exitosamente.\", \"nuevoMontoRestante\": " + nuevoMontoRestante + "}").build();

    } catch (Exception e) {
        if (transaction != null) {
            transaction.rollback();
        }
        e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"Error al registrar el abono: " + e.getMessage() + "\"}")
                .build();
    }
}

}
