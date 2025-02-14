package Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ModelBD.ClientesBD;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Path("Clientes")
public class Clientes {

    private static SessionFactory sessionFactory;

    static {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @POST
    @Path("guardar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response guardar(@FormParam("primer_nombre") String primer_nombre,
                            @FormParam("segundo_nombre") String segundo_nombre,
                            @FormParam("apellido_paterno") String apellido_paterno,
                            @FormParam("apellido_materno") String apellido_materno,
                            @FormParam("fecha_nacimiento")String fecha_nacimiento,
                            @FormParam("telefono") String telefono,
                            @FormParam("correo") String correo,
                            @FormParam("direccion") String direccion,
                            @FormParam("activo") int activo) {

        if (primer_nombre == null || primer_nombre.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El nombre es obligatorio.\"}")
                    .build();
        }
        if (segundo_nombre == null || segundo_nombre.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El nombre es obligatorio.\"}")
                    .build();
        }        
        if (apellido_paterno == null || apellido_paterno.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El apellido es obligatorio.\"}")
                    .build();
        }
        if (apellido_materno == null || apellido_materno.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El apellido es obligatorio.\"}")
                    .build();
        }
        if (correo == null || correo.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El correo es obligatorio.\"}")
                    .build();
        }
        if (telefono == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El telefono es obligatorio.\"}")
                    .build();
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"La direccion es obligatoria.\"}")
                    .build();
        }
        if (activo != 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El campo 'activo' debe ser 1.\"}")
                    .build();
        }

        Session session = null;
        Transaction transaction = null;

        try {
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
         Date fechaNacimientoParsed = dateFormat.parse(fecha_nacimiento);

            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            ClientesBD cliente = new ClientesBD();
            cliente.setPrimer_nombre(primer_nombre);
            cliente.setSegundo_nombre(segundo_nombre);
            cliente.setApellido_paterno(apellido_paterno);
            cliente.setApellido_materno(apellido_materno);
            cliente.setFecha_nacimiento(fecha_nacimiento);
            cliente.setCorreo(correo);
            cliente.setTelefono(telefono);
            cliente.setDireccion(direccion);
            cliente.setActivo(activo == 1);

            session.save(cliente);
            transaction.commit();

            return Response.ok("{\"message\":\"Cliente guardado exitosamente\"}").build();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error al guardar el cliente: " + e.getMessage() + "\"}")
                    .build();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @GET
    @Path("eliminar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminar(@QueryParam("id") Long id) {
        Session session = null;
        Transaction transaction = null;

        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El campo 'id' es obligatorio y debe ser mayor a cero.\"}")
                    .build();
        }

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            ClientesBD cliente = session.get(ClientesBD.class, id);

            if (cliente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Cliente no encontrado con id: " + id + "\"}")
                        .build();
            }

            cliente.setActivo(false);
            session.update(cliente);
            transaction.commit();

            return Response.ok("{\"message\":\"Cliente desactivado exitosamente\"}").build();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error al desactivar el cliente: " + e.getMessage() + "\"}")
                    .build();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @POST
    @Path("editar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editar(@FormParam("id") Long id,
                           @FormParam("primer_nombre") String primer_nombre,
                           @FormParam("segundo_nombre") String segundo_nombre,
                           @FormParam("apellido_paterno") String apellido_paterno,
                           @FormParam("apellido_materno") String apellido_materno,
                           @FormParam("fecha_nacimiento")String fecha_nacimiento,
                           @FormParam("telefono") String telefono,
                           @FormParam("correo") String correo,
                           @FormParam("direccion") String direccion,
                            @FormParam("activo") int activo)  {

        if (id == null || id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El campo 'id' es obligatorio y debe ser mayor a cero.\"}")
                    .build();
        }

        Session session = null;
        Transaction transaction = null;

        try {
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
         Date fechaNacimientoParsed = dateFormat.parse(fecha_nacimiento);

            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            ClientesBD cliente = session.get(ClientesBD.class, id);

            if (cliente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Cliente no encontrado.\"}")
                        .build();
            }

        if (primer_nombre == null || primer_nombre.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El nombre es obligatorio.\"}")
                    .build();
        }
        if (segundo_nombre == null || segundo_nombre.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El nombre es obligatorio.\"}")
                    .build();
        }        
        if (apellido_paterno == null || apellido_paterno.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El apellido es obligatorio.\"}")
                    .build();
        }
        if (apellido_materno == null || apellido_materno.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El apellido es obligatorio.\"}")
                    .build();
        }
        if (correo == null || correo.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El correo es obligatorio.\"}")
                    .build();
        }
        if (telefono == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El telefono es obligatorio.\"}")
                    .build();
        }
        if (direccion == null || direccion.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"La direccion es obligatoria.\"}")
                    .build();
        }
        if (activo != 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El campo 'activo' debe ser 1.\"}")
                    .build();
        }

            session.update(cliente);
            transaction.commit();

            return Response.ok("{\"message\":\"Cliente actualizado exitosamente\"}").build();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error al actualizar el cliente: " + e.getMessage() + "\"}")
                    .build();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
