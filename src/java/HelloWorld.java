import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
public class HelloWorld extends HttpServlet {
public void doGet(HttpServletRequest req, HttpServletResponse res)
throws ServletException, IOException {
res.setContentType("text/html");
PrintWriter out = res.getWriter();
out.println("<HTML>");
out.println("<HEAD><TITLE>Bonjour tout le monde</TITLE></HEAD>");
out.println("<BODY>");
out.println("<BIG>Bonjour tout le monde</BIG>");
out.println("</BODY></HTML>");

}
}