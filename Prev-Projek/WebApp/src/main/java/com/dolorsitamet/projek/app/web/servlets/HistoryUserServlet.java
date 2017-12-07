package com.dolorsitamet.projek.app.web.servlets;

import com.dolorsitamet.projek.app.web.service.CookieManager;
import com.dolorsitamet.projek.app.web.service.Identity;
import com.dolorsitamet.projek.app.web.ws.Exception_Exception;
import com.dolorsitamet.projek.app.web.ws.HistoryService;
import com.dolorsitamet.projek.app.web.ws.HistoryServiceImplService;
import com.dolorsitamet.projek.app.web.ws.Transaction;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "HistoryUserServlet", urlPatterns = {"/history_user"}, loadOnStartup = 1)
public class HistoryUserServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Identity id = CookieManager.auth(request, response);
    if (id == null) {
      return;
    }

    // Set username.
    request.setAttribute("username", id.name);

    HistoryServiceImplService historyService = new HistoryServiceImplService();
    HistoryService history = historyService.getHistoryServiceImplPort();

    try {
      List<Transaction> transactions = history.getVisibleUserHistory(id.token);

      request.setAttribute("transactions", transactions);
      request.getRequestDispatcher("history_user.jsp").forward(request, response);
    } catch (Exception_Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Identity id = CookieManager.auth(request, response);
    if (id == null) {
      return;
    }

    HistoryServiceImplService historyService = new HistoryServiceImplService();
    HistoryService history = historyService.getHistoryServiceImplPort();

    try {
      long transactionId = Long.parseLong(request.getParameter("hide_transaction_id"));

      history.hideUserHistoryItem(id.token, transactionId);
    } catch (Exception e) {
      e.printStackTrace();
    }

    response.sendRedirect("history_user");
  }
}
