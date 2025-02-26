package Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import ModelBD.UsuariosBD;
import Service.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@Path("Login")
@Produces(MediaType.APPLICATION_JSON)
public class Login {
    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

@POST
@Path("usuarios")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public Response autenticar(@FormParam("nombre_usuario") String nombre_usuario,
                           @FormParam("password") String password) {
    Session session = null;

    if (nombre_usuario == null || nombre_usuario.trim().isEmpty() ||
        password == null || password.trim().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                       .entity("{\"error\":\"Nombre de usuario y password son obligatorios.\"}")
                       .build();
    }

    try {
        session = sessionFactory.openSession();

        String hql = "FROM UsuariosBD u WHERE u.nombreUsuario = :nombre AND u.password = :password AND u.activo = 1";
        UsuariosBD usuario = (UsuariosBD) session.createQuery(hql)
                                                 .setParameter("nombre", nombre_usuario)
                                                 .setParameter("password", password)
                                                 .uniqueResult();

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity("{\"error\":\"Usuario o password incorrectos.\"}")
                           .build();
        }

        return Response.ok("{\"message\":\"Login exitoso\", \"id_usuario\": " + usuario.getId() + "}")
                       .build();
    } catch (Exception e) {
        // Imprimir la traza completa de la excepción en los logs del servidor
        e.printStackTrace(); // Esto imprime la traza completa en los logs de Tomcat

        // También puedes incluir el mensaje de error completo en la respuesta, si deseas
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity("{\"error\":\"Error al procesar la solicitud de login.\", \"exception\": \"" + e.getMessage() + "\"}")
                       .build();
    } finally {
        if (session != null) session.close();
    }
}
}

