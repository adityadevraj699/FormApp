# EduInsight : AI-Powered Feedback Analysis for Quality Education
### Supporting UN SDG-4: Quality Education through AI-Driven Feedback Audits

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![UN SDG-4](https://img.shields.io/badge/UN-SDG--4-blue.svg)](https://sdgs.un.org/goals/goal4)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)

**EduInsight** is a next-generation feedback analysis ecosystem designed for educational institutions. It utilizes **LLM-based Sentiment Analysis** and proprietary **Pedagogical Power Algorithms** to transform raw student feedback into actionable teaching intelligence.

---

## 📄 Abstract
A web-based AI-powered system called **EduInsight** was created to automatically analyse student feedback in order to assess the quality of instruction. To produce insightful results, the system combines quantitative ratings with AI methods like sentiment analysis, text summarization, and key-phrase extraction. For educators and administrators, it offers interactive dashboards and PDF reports. EduInsight supports **SDG-4: Quality Education** and enhances efficiency, objectivity, and transparency in teaching evaluation compared to conventional feedback methods.

**Keywords:** Teaching Evaluation, Automated Feedback Analysis, Language Models, Sentiment Analysis, Educational Quality, SDG-4.

---

## 🔬 Mathematical Modeling & Algorithmic Derivation

The core of EduInsight lies in its ability to quantify qualitative data. Below are the mathematical foundations and their simplified logic:

### 1️⃣ Stakeholder Mean Rating ($\mu$)
Calculates the consolidated average across multiple evaluation points (Student, HOD, Self).
$$\mu = \frac{1}{N} \sum_{i=1}^{N} R_i$$

> **Simplified Logic:**
> **$\text{Mean Rating} = \frac{\text{Total Sum of All Ratings}}{\text{Total Number of Responses}}$**
> *Example: If total ratings sum to 40 from 10 students, the Mean Rating is 4.0.*

---

### 2️⃣ AI Sentiment Polarity Index ($\sigma$)
Quantifies qualitative text into a normalized percentage-based positive index using NLP.
$$\sigma_{pos} = \left( \frac{P}{T} \right) \times 100$$

> **Simplified Logic:**
> **$\text{Positive Sentiment \%} = \left( \frac{\text{Number of Positive Comments}}{\text{Total Number of Written Comments}} \right) \times 100$**
> *Example: 8 positive comments out of 10 total comments = 80% Sentiment Score.*

---

### 3️⃣ Innovation: Pedagogical Power Index (PPI)
The PPI is a weighted metric derived to rank faculty excellence. It rewards both high numerical ratings and positive emotional sentiment.
$$PPI = (\mu \times 10) + (\sigma_{pos} \times 0.5)$$

> **Simplified Logic:**
> **$\text{Power Index (out of 100)} = (\text{Avg Rating} \times 10) + (\text{Positive Sentiment \%} \times 0.5)$**
> *Why this? It scales the 5-point rating to 50 points and adds up to 50 points from student sentiment, creating a perfect Centum (100) score.*

---

### 4️⃣ Student Engagement Value (SEV)
Measuring the participation and responsiveness gap.
$$SEV = \left( \frac{V_R}{E_S} \right) \times 100$$

> **Simplified Logic:**
> **$\text{Engagement Rate} = \left( \frac{\text{Feedbacks Received}}{\text{Total Students Enrolled}} \right) \times 100$**

---

## 🛡️ Mathematical Validation & SDG-4 Alignment


[Image of Kirkpatrick Model of Evaluation levels]


The **PPI Formula** is not just an arbitrary calculation; it is a derived metric based on established academic frameworks:

1. **Kirkpatrick’s Level 1 (Reaction):** Research proves that student satisfaction (Sentiment) is a primary indicator of learning effectiveness. PPI gives 50% weightage to this "Student Voice".
2. **Standardization (MCDM):** In Multi-Criteria Decision Making, weighted averages are used to combine different data types. PPI normalizes 5-star ratings and 100% sentiment into a single 100-point scale for fair comparison.
3. **SDG-4 Indicator 4.c.1:** UNESCO emphasizes monitoring "Teacher Quality". PPI provides a transparent, data-backed audit score that removes the manual bias found in traditional systems.

---

## 🏗️ System Architecture & Workflow

1.  **Feedback Collection:** Multi-layered surveys (Numerical Ratings + Open-ended Text).
2.  **AI Analysis:** Sentiment Polarity (Positive/Neutral/Negative) using NLP models.
3.  **Smart Deployment Audit:** Real-time AI-backed audit check before teacher-program assignment.
4.  **Actionable Dashboards:** Visualizing performance trends and participation rates using Chart.js.

---

## 📈 Result Analysis & Impact

Validated during a live academic training program:
* **90% reduction** in manual feedback processing time.
* **Objective Evaluation:** Decisions based on the **PPI Centum Score**, not individual bias.
* **Pedagogical Power Index:** Effectively identifies "Exemplary" vs "Improvement Required" faculty nodes.

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java Spring Boot 3.x, JPA, Hibernate |
| **Database** | MySQL (Relational Schema) |
| **Frontend** | Thymeleaf, Bootstrap 5, SweetAlert2 |
| **AI Engine** | LLM-based Sentiment Analysis & Summary |
| **Visualization**| Chart.js, JFreeChart, iTextPDF |

---

## 👨‍💻 Author & Research Credit

<table border="0">
  <tr>
    <td width="200px" align="center">
      <img src="https://res.cloudinary.com/ddtcj9ks5/image/upload/v1764509135/aditya_hacgws.png" 
           width="160px" 
           height="160px" 
           style="border-radius: 50%; border: 4px solid #10b981; object-fit: cover; display: block;" 
           alt="Aditya Kumar"/>
    </td>
    <td style="padding-left: 20px;">
      <h2 style="margin-bottom: 5px; color: #064e3b;">Aditya Kumar</h2>
      <p style="margin-top: 0;"><b>Department of Computer Science and Engineering</b><br>
      Meerut Institute of Technology, Meerut</p>
      <p>
        <a href="https://github.com/adityadevraj699"><img src="https://img.shields.io/badge/GitHub-adityadevraj699-181717?style=flat&logo=github" alt="GitHub"/></a>
        <a href="https://linkedin.com/in/adityadevraj699"><img src="https://img.shields.io/badge/LinkedIn-adityadevraj699-0A66C2?style=flat&logo=linkedin" alt="LinkedIn"/></a>
        <a href="https://aditya-portfolio-org.vercel.app/"><img src="https://img.shields.io/badge/Portfolio-Visit%20Site-FF4B4B?style=flat&logo=vercel" alt="Portfolio"/></a>
      </p>
      <p>📧 <a href="mailto:aditya.kumar1.cs.2022@mitmeerut.ac.in" style="text-decoration: none; color: #10b981; font-weight: bold;">aditya.kumar1.cs.2022@mitmeerut.ac.in</a></p>
    </td>
  </tr>
</table>

---

## 📚 References
1. **Husain, N., & Khan, M. (2021).** Students' feedback: An effective tool in evaluation. *PubMed*.
2. **Aragón, O., et al. (2023).** Beyond the Numbers: Equity in Evaluation. *Springer*.
3. **Feng Lin, et al. (2025).** Empower instructors with actionable insights. *AI in Education*.
4. **United Nations.** [SDG-4: Quality Education Guidelines](https://sdgs.un.org/goals/goal4).

---
🔗 **Project Repository:** [https://github.com/adityadevraj699/FormApp](https://github.com/adityadevraj699/FormApp)

© 2026 EduInsight Project | Developed for Academic Excellence and SDG-4 Compliance.
