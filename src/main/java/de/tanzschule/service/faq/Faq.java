package de.tanzschule.service.faq;

import de.tanzschule.service.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "faq")
public class Faq extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    protected Faq() {
    }

    public Faq(String question, String answer, int displayOrder) {
        super();
        this.question = question;
        this.answer = answer;
        this.displayOrder = displayOrder;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
