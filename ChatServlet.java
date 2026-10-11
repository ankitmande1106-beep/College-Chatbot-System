package com.college.chatbot;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Main servlet to handle chat functionality.
 * Implements intent detection and session history.
 */
@WebServlet(name = "ChatServlet", urlPatterns = {"/chat"})
public class ChatServlet extends HttpServlet {

    private final FAQDatabase faqDb = FAQDatabase.getInstance();

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("history".equals(action)) {
            HttpSession session = request.getSession();
            List<JSONObject> history = null;
            if (session != null) {
                history = (List<JSONObject>) session.getAttribute("chatHistory");
            }
            
            JSONObject jsonResponse = new JSONObject();
            JSONArray historyArray = new JSONArray();

            if (history != null) {
                for (JSONObject msg : history) {
                    // Re-create to avoid serialization issues, sometimes JSONObjects hold references
                    JSONObject copy = new JSONObject();
                    copy.put("sender", msg.getString("sender"));
                    copy.put("text", msg.getString("text"));
                    historyArray.put(copy);
                }
            }
            jsonResponse.put("history", historyArray);

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(jsonResponse.toString());
            out.flush();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message = request.getParameter("message");
        if (message == null) message = "";

        // Basic AI Intent evaluation via Database lookup
        String botReply = faqDb.getResponse(message);

        // Session history tracking
        HttpSession session = request.getSession(true);
        List<JSONObject> history = (List<JSONObject>) session.getAttribute("chatHistory");
        if (history == null) {
            history = new ArrayList<>();
        }

        JSONObject userMsg = new JSONObject();
        userMsg.put("sender", "user");
        userMsg.put("text", message);
        history.add(userMsg);

        JSONObject botMsg = new JSONObject();
        botMsg.put("sender", "bot");
        botMsg.put("text", botReply);
        history.add(botMsg);

        session.setAttribute("chatHistory", history);

        // Respond back to frontend
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("reply", botReply);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}
