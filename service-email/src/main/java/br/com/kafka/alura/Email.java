package br.com.kafka.alura;

public class Email {

    private final String subject;
    private final String body;

    public Email(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }
}
