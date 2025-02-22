package Service;

import ModelBD.PrestamosBD;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("Prestamos")
public class Prestamos {

    private static final SessionFactory sessionFactory;

    static {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @POST
    @Path("guardar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response guardar(@FormParam("id_cliente") Long id_cliente,
                            @FormParam("id_asesor") Long id_asesor,
                            @FormParam("monto_prestado") double monto_prestado,
                            @FormParam("tasa_interes") int tasa_interes,
                            @FormParam("plazo") int plazo ) {

        if (id_cliente == null || id_cliente <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El campo 'id_cliente' es obligatorio y debe ser mayor a cero.\"}")
                    .build();
        }
        if (id_asesor == null || id_asesor <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El campo 'id_asesor' es obligatorio y debe ser mayor a cero.\"}")
                    .build();
        }
        if (monto_prestado <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El monto prestado debe ser mayor a cero.\"}")
                    .build();
        }
        if ( tasa_interes<= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"La taza de interes debe ser mayor a cero.\"}")
                    .build();
        }
        if (plazo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El plazo debe ser mayor a cero.\"}")
                    .build();
        }

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            
            PrestamosBD prestamo = new PrestamosBD();
            prestamo.setId_cliente(id_cliente);
            prestamo.setId_asesor(id_asesor);
            prestamo.setMonto_prestado(monto_prestado);
            prestamo.setTasa_interes(tasa_interes);
            prestamo.setPlazo(plazo);
            prestamo.setMonto_faltante(monto_prestado);
            prestamo.setFecha_prestamo(new Date());
            prestamo.setActivo(true);

            session.save(prestamo);
            transaction.commit();

            return Response.ok("{\"message\":\"El prastamo se ha guardado correctamente.\"}").build();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No se logro guardar el prestamo: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
