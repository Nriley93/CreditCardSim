package servlets;

import business.CreditCard;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author n.riley
 */
public class AccountActionServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String URL="/CardTrans.jsp";
        String msg="", chgDesc, req;
        CreditCard card;
        int acctno;
        double transamt;
        try {
//            Retrieveing input and deciding response based on current action
            String path = getServletContext().getRealPath("/WEB-INF/") + "\\";
            card = (CreditCard) request.getSession().getAttribute("card");
            String action = request.getParameter("actiontype");
            if(card == null &&
                    !action.equalsIgnoreCase("NEW") &&
                    !action.equalsIgnoreCase("EXISTING")) {
                msg = "Action attempt on unopened account.<br>";
            } else {
                if(action.equalsIgnoreCase("NEW")) {
                    card = new CreditCard(path);
                    if(card.getErrorStatus()) {
                        msg += "Account open error: " +
                                card.getErrorMessage() + "<br>";
                    } else {
                        msg += card.getActionMsg()+"<br>";
                    }  
                }
                else if(action.equalsIgnoreCase("EXISTING")) {
                    acctno = Integer.parseInt(request.getParameter("account"));
                    card = new CreditCard(acctno,path);
                    if(card.getErrorStatus()) {
                        msg += "Existing Account open error: " +
                                card.getErrorMessage() + "<br>";
                    } else {
                        msg += card.getActionMsg()+"<br>";
                    }  
                }
                if(action.equalsIgnoreCase("HISTORY")) {
                    URL = "/History.jsp";
                }
//                if card built, decide which action is being placed
                if(card != null) {
                    if(action.equalsIgnoreCase("CHARGE")) {
                        try {
                            req = request.getParameter("cAmt").trim();
                            if(req.isEmpty()) {
                                msg += card.getErrorMessage() + "<br>";
                            }
                            else {
                                transamt = Double.parseDouble(req);
                                chgDesc = request.getParameter("cDesc");
                                card.setCharge(transamt,chgDesc);
                                msg += card.getActionMsg() + "<br>";
                            }
                        } catch(NumberFormatException e) {
                            msg += "Charge Error: "+e.getMessage()+"<br>";
                        }
                    }
                    if(action.equalsIgnoreCase("PAYMENT")) {
                        try{
                            req = request.getParameter("pAmt").trim();
                            if(req.isEmpty()) {
                                msg += card.getErrorMessage() + "<br>";
                            } else {
                                transamt = Double.parseDouble(req);
                                card.setPayment(transamt);
                                msg += card.getActionMsg() + "<br>";
                            }
                        }catch(NumberFormatException e) {
                            msg += "Payment Error: "+e.getMessage()+"<br>";
                        }
                    }
                    if(action.equalsIgnoreCase("INCREASE")) {
                        try{
                            req = request.getParameter("cIncrease").trim();
                            if(req.isEmpty()) {
                                msg += card.getActionMsg();
                            } else {
                                transamt = Double.parseDouble(req);
                                card.setCreditIncrease(transamt);
                                msg += card.getActionMsg() + "<br>";
                            }
                        }catch(NumberFormatException e) {
                            msg += "Credit Increase Error: "
                                    +e.getMessage()+"<br>";
                        }
                    }   
                    if(action.equalsIgnoreCase("INTEREST")) {
                        try {
                        req = request.getParameter("iRate").trim();
                        if(req.isEmpty()) {
                            msg += card.getErrorMessage();
                        } else {
                            transamt = Double.parseDouble(req);
                            card.setInterestCharge(transamt);
                            msg += card.getActionMsg() + "<br>";
                        }
                        }catch(NumberFormatException e) {
                            msg += "Interest Rate Error: "
                                    +e.getMessage()+"<br>";
                        }
                    }
                }                
                request.getSession().setAttribute("card", card);
                Cookie acct = 
                        new Cookie("acct",String.valueOf(card.getAccountId()));
                acct.setPath("/");
                acct.setMaxAge(60*2);
                response.addCookie(acct);
            }
        } catch(NumberFormatException e) {
            msg += "Servlet error: " + e.getMessage() + "<br>";
        }
        request.setAttribute("msg", msg);
        RequestDispatcher disp = 
                getServletContext().getRequestDispatcher(URL);
        disp.forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
