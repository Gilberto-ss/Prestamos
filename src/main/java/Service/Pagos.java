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
                            @FormParam("id_cliente") Long id_cliente,
                            @FormParam("abono") double abono){

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

            PagosBD pagos = new PagosBD();
            pagos.setId_prestamo(id_prestamo);
            pagos.setId_asesor(id_asesor);
            pagos.setId_cliente(id_cliente);
            pagos.setAbono(0);
            pagos.setActivo(true);

            session.save(pagos);
            transaction.commit();

            return Response.ok("{\"message\":\"El pagos se ha guardado correctamente.\"}").build();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No se logro guardar el pagos: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
