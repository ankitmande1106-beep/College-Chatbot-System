package com.college.chatbot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory FAQ database.
 * Uses keyword matching for known questions and Ollama AI (Llama3) as fallback.
 */
public class FAQDatabase {
    private static final FAQDatabase instance = new FAQDatabase();

    private final Map<String, String> qnaMap;
    private final OllamaAIService aiService;

    private FAQDatabase() {
        qnaMap = new ConcurrentHashMap<>();
        aiService = OllamaAIService.getInstance();
    }

    public static FAQDatabase getInstance() {
        return instance;
    }

    public Map<String, String> getAllFAQs() {
        return qnaMap;
    }

    public void addOrUpdateFAQ(String keyword, String response) {
        qnaMap.put(keyword.toLowerCase(), response);
    }

    public void deleteFAQ(String keyword) {
        qnaMap.remove(keyword.toLowerCase());
    }

    /**
     * Pattern matching to find best intent.
     * Falls back to Ollama AI (Llama3) when no keyword matches.
     */
    public String getResponse(String rawMessage) {
        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            return "Please say something.";
        }

        String message = rawMessage.trim().toUpperCase();
        String reply;

        if (message.contains("HIGHEST PACKAGE")) {
            reply = "The highest package offered was ₹12 LPA.";

        } else if (message.contains("AVERAGE PACKAGE")) {
            reply = "The average IT package is ₹4.5 LPA.";

        } else if (message.contains("PLACEMENT PERCENTAGE")) {
            reply = "The IT department placement percentage is 85%.";

        } else if (message.contains("COMPANIES VISIT")) {
            reply = "TCS, Infosys, Wipro and Capgemini visited last year.";

        } else if (message.contains("ADMISSION")) {
            reply = "Admission starts in June.";

        } else if (message.contains("FEES") || message.contains("FEE") || message.contains("PAYMENT")) {
            reply = "The annual fees is ₹1 lakh.";

        } else if (message.contains("COURSES") || message.contains("COURSE") || message.contains("OPTIONS")) {
            reply = "We offer INFT, CMPN, EXTC, ECS and ELC.";

        } else if (message.contains("INFT") || message.contains("INFORMATION") || message.equals("IT")) {
            reply = "Yes, we offer this course! Do you want to know more about it?";

        } else if (message.equals("YES") || message.equals("YESS")) {
            reply = "It is a 4-year course which includes 8 semesters.";

        } else if (message.contains("LIBRARY TIMING")) {
            reply = "Library is open from 9:00 AM to 5:00 PM.";

        } else if (message.contains("LIBRARY LOCATION")) {
            reply = "Library is located on the 2nd and 4th floor.";

        } else if (message.contains("WIFI") || message.contains("WI-FI")) {
            reply = "Yes, high-speed Wi-Fi is available.";

        } else if (message.contains("HOSTEL")) {
            reply = " NO Hostel facility is available.";

        } else if (message.contains("GYM")) {
            reply = "Gym is available. Timings is after 9am.";

        } else if (message.contains("SPORTS")) {
            reply = "Cricket, football, badminton available.";

        } else if (message.contains("MEDICAL")) {
            reply = "Medical room is available.";

        } else if (message.contains("PARKING")) {
            reply = "Parking is available.";

        } else if (message.contains("EXAM ") || message.contains("EXAM SCHEDULE")) {
            reply = "Check website for exam schedule.";

        } else if (message.contains("TIMETABLE")) {
            reply = "Timetable available on portal.";

        } else if (message.contains("RESULT")) {
            reply = "Results will be declared on college notice board.";

        } else if (message.contains("HALL TICKET")) {
            reply = "Download from portal.";

        } else if (message.contains("REVALUATION")) {
            reply = "Apply within 7 days.";

        } else if (message.contains("BACKLOG") || message.contains("KT")) {
            reply = "Reappear next semester.";

        } else if (message.contains("ATTENDANCE")) {
            reply = "75% attendance required.";

        } else if (message.contains("HOD")) {
            reply = "Hod changes according to year and department";

        } else if (message.contains("PROGRAMMING")) {
            reply = "C, C++, Java, Python.";

        } else if (message.contains("LAB")) {
            reply = "Programming, Network, Project labs.";

        } else if (message.contains("HACKATHON")) {
            reply = "Conducted every year.";

        } else if (message.contains("CERTIFICATION")) {
            reply = "AWS, Python, Cyber Security.";

        } else if (message.contains("ANNUAL FEST")) {
            reply = "Fest in April which is named as RYTHM EMBER.";

        } else if (message.contains("TECHNICAL EVENT")) {
            reply = "Conducted every semester.";

        } else if (message.contains("NSS") || message.contains("NCC")) {
            reply = "Both available.";

        } else if (message.contains("CONTACT")) {
            reply = "022-12345678";

        } else if (message.contains("APP")) {
            reply = "NO mobile app is not avilable";

        } else if (message.equals("HI") || message.equals("HELLO") || message.equals("HII")) {
            reply = "HELLO!";

        } else {
            // 🤖 AI Fallback — uses local Ollama (Llama3) model
            reply = aiService.getResponse(rawMessage);
        }

        return reply;
    }
}
