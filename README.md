# EduInsight : AI-Powered Feedback Analysis for Quality Education
### Supporting UN SDG-4: Quality Education through AI-Driven Feedback Audits

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![UN SDG-4](https://img.shields.io/badge/UN-SDG--4-blue.svg)](https://sdgs.un.org/goals/goal4)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)

**EduInsight** is a next-generation feedback analysis ecosystem designed for educational institutions. It utilizes **LLM-based Sentiment Analysis** and proprietary **Pedagogical Power Algorithms** to transform raw student feedback into actionable teaching intelligence.

---

## 📄 Abstract
A web-based AI-powered system called **EduInsight** was created to automatically analyse student feedback in order to assess the quality of instruction. To produce insightful results, the system combines quantitative ratings with AI methods like sentiment analysis, text summarization, and key-phrase extraction. For educators and administrators, it offers interactive dashboards and PDF reports. EduInsight supports **SDG-4: Quality Education** and enhances efficiency, objectivity, and transparency in teaching evaluation compared to conventional feedback methods.

**Keywords:** Teaching Evaluation, Automated Feedback Analysis, Language Models, Sentiment Analysis, Educational Quality, SDG-4.

---

## 🔬 Mathematical Modeling & Algorithmic Derivation

The core of EduInsight lies in its ability to quantify qualitative data. Below are the mathematical foundations used in the system:

### 1️⃣ Stakeholder Mean Rating ($\mu$)
Calculates the consolidated average across multiple evaluation points (Student, HOD, Self).
$$\mu = \frac{1}{N} \sum_{i=1}^{N} R_i$$
*Where $R_i$ is the individual rating and $N$ is the total response count.*

### 2️⃣ AI Sentiment Polarity Index ($\sigma$)
Quantifies qualitative text into a normalized percentage-based positive index using NLP.
$$\sigma_{pos} = \left( \frac{P}{T} \right) \times 100$$
*Where $P$ = Count of Positive Labels, $T$ = Total Textual Feedbacks.*

### 3️⃣ Innovation: Pedagogical Power Index (PPI)
The PPI is a weighted metric derived to rank faculty excellence. It rewards both high numerical ratings and positive emotional sentiment.
$$PPI = (\mu \times 10) + (\sigma_{pos} \times 0.5)$$
*This formula scales the 5-point rating to a 50-point base and adds up to 50 points from sentiment, resulting in a **Centum Score (0-100)**.*

### 4️⃣ Student Engagement Value (SEV)
$$SEV = \left( \frac{V_R}{E_S} \right) \times 100$$
*Where $V_R$ is Validated Responses and $E_S$ is Total Enrolled Students.*

---

## 🏗️ System Architecture & Workflow



1.  **Feedback Collection:** Multi-layered surveys (Numerical + Open-ended Text).
2.  **Dual-Pipeline Processing:**
    * **Statistical Pipeline:** Computes Mean, Variance, and Participation Ratios.
    * **AI Pipeline:** Utilizes LLMs for Sentiment Classification and Key-Phrase Extraction.
3.  **Smart Deployment Audit:** Admins use a real-time audit tool that cross-references the **PPI score** before assigning teachers to programs.
4.  **Visualization:** High-fidelity dashboards using Chart.js for web and JFreeChart for PDF generation.

---

## 🚀 Key Innovation Features

- **🧠 Smart AI Audit Popups:** Real-time pedagogical audit alerts during administrative tasks.
- **📊 Multi-Line F-Index Analytics:** Advanced feedback timeline charts for multi-program tracking.
- **📈 Growth Trend Analysis:** Identifies if teaching quality is "UPWARD", "STABLE", or "DOWNWARD".
- **🔍 Qualitative Voice Logs:** AI-filtered student suggestions categorized by sentiment.
- **📄 Professional PDF Engine:** High-resolution audit reports with competency radar maps.

---

## 📈 Result Analysis & Impact

Validated during a live academic training program:
* **Stakeholder Consensus:** Average student rating of **4.17** vs HOD rating of **4.0**.
* **PPI Stability:** Achieved a consistent performance score of **4.25/5**.
* **Operational Efficiency:** **90% reduction** in manual feedback processing time.
* **SDG-4 Alignment:** Directly improves classroom quality by identifying critical pedagogical gaps.

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java Spring Boot 3.x, Spring Data JPA, Hibernate |
| **Database** | MySQL (Relational Schema) |
| **Frontend** | Thymeleaf, Bootstrap 5, SweetAlert2 |
| **AI/NLP** | LLM APIs, Reactive WebClient, Sentiment Analysis |
| **Reporting** | iTextPDF, JFreeChart, Chart.js |

---

## 👨‍💻 Author & Research Credit

<table border="0">
  <tr>
    <td width="200px">
      <img src="https://res.cloudinary.com/ddtcj9ks5/image/upload/v1764509135/aditya_hacgws.png" width="180px" style="border-radius: 20px; border: 3px solid #10b981;" alt="Aditya Kumar"/>
    </td>
    <td>
      <h3>Aditya Kumar</h3>
      <p><b>Department of Computer Science and Engineering</b><br>
      Meerut Institute of Technology, Meerut</p>
      <p>
        <a href="https://github.com/adityadevraj699"><img src="https://img.shields.io/badge/GitHub-adityadevraj699-181717?style=flat&logo=github" alt="GitHub"/></a>
        <a href="https://linkedin.com/in/adityadevraj699"><img src="https://img.shields.io/badge/LinkedIn-adityadevraj699-0A66C2?style=flat&logo=linkedin" alt="LinkedIn"/></a>
        <a href="https://aditya-portfolio-org.vercel.app/"><img src="https://img.shields.io/badge/Portfolio-Visit%20Site-FF4B4B?style=flat&logo=vercel" alt="Portfolio"/></a>
      </p>
      <p>📧 <a href="mailto:aditya.kumar1.cs.2022@mitmeerut.ac.in">aditya.kumar1.cs.2022@mitmeerut.ac.in</a></p>
    </td>
  </tr>
</table>

---

## 📚 References
1.  **Husain, N., & Khan, M. (2021).** Students' feedback: An effective tool in evaluation. *PubMed*.
2.  **Aragón, O., et al. (2023).** Beyond the Numbers: Equity in Evaluation. *Springer*.
3.  **Feng Lin, et al. (2025).** Empower instructors with actionable insights. *AI in Education*.
4.  **United Nations.** [SDG-4: Quality Education Guidelines](https://sdgs.un.org/goals/goal4).

---
🔗 **Project Repository:** [https://github.com/adityadevraj699/FormApp](https://github.com/adityadevraj699/FormApp)

© 2026 EduInsight Project | Developed for Academic Excellence and SDG-4 Compliance.
