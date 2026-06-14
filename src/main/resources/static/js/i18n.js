const translations = {
    en: {
        navHome: "Home",
        navDashboard: "Dashboard",
        navSymptom: "Symptom Checker",
        navReminder: "Reminders",
        navHistory: "History",
        brandName: "CareSync AI",
        heroTitle: "Your Trusted AI Healthcare Assistant",
        heroSub: "Professional, secure, and fast healthcare analysis at your fingertips.",
        btnTrySymptom: "Try Symptom Checker",
        btnSetupReminders: "Setup Reminders",
        sympTitle: "AI Symptom Checker",
        sympDesc: "Describe your symptoms below for a preliminary AI assessment.",
        btnAnalyze: "Analyze Symptoms",
        rmTitle: "Add Medication Reminder",
        rmMedName: "Medicine Name",
        rmDosage: "Dosage",
        rmTime: "Time",
        rmFreq: "Frequency",
        btnAddRm: "Add Reminder",
        rmSchedule: "Your Medical Schedule",
        dashTitle: "Health Analytics Dashboard",
        dashChecks: "Symptom Checks",
        dashMeds: "Active Medications",
        historyTitle: "Your Medical History",
        langToggle: "EN | TA"
    },
    ta: {
        navHome: "முகப்பு",
        navDashboard: "கட்டுப்பாட்டு அறை",
        navSymptom: "அறிகுறி சரிபார்ப்பு",
        navReminder: "நினைவூட்டல்கள்",
        navHistory: "வரலாறு",
        brandName: "கேர்சின்க் AI",
        heroTitle: "உங்கள் நம்பகமான AI சுகாதார உதவியாளர்",
        heroSub: "தொழில்முறை, பாதுகாப்பான மற்றும் விரைவான சுகாதார பகுப்பாய்வு உங்கள் விரல் நுனியில்.",
        btnTrySymptom: "அறிகுறி சரிபார்ப்பை முயற்சிக்கவும்",
        btnSetupReminders: "நினைவூட்டல்களை அமைக்கவும்",
        sympTitle: "AI அறிகுறி சரிபார்ப்பு",
        sympDesc: "ஆரம்பகால AI மதிப்பீட்டிற்கு உங்கள் அறிகுறிகளை கீழே விவரிக்கவும்.",
        btnAnalyze: "அறிகுறிகளை பகுப்பாய்வு செய்",
        rmTitle: "மருந்து நினைவூட்டலைச் சேர்க்கவும்",
        rmMedName: "மருந்தின் பெயர்",
        rmDosage: "அளவு",
        rmTime: "நேரம்",
        rmFreq: "அடிக்கடி (Frequency)",
        btnAddRm: "நினைவூட்டலைச் சேர்க்கவும்",
        rmSchedule: "உங்கள் மருத்துவ அட்டவணை",
        dashTitle: "சுகாதார பகுப்பாய்வு கட்டுப்பாட்டு அறை",
        dashChecks: "அறிகுறி சோதனைகள்",
        dashMeds: "செயலில் உள்ள மருந்துகள்",
        historyTitle: "உங்கள் மருத்துவ வரலாறு",
        langToggle: "TA | EN"
    }
};

let currentLang = localStorage.getItem('lang') || 'en';

function toggleLanguage(e) {
    if(e) e.preventDefault();
    currentLang = currentLang === 'en' ? 'ta' : 'en';
    localStorage.setItem('lang', currentLang);
    applyTranslations();
}

function applyTranslations() {
    document.querySelectorAll('[data-i18n]').forEach(element => {
        const key = element.getAttribute('data-i18n');
        if (translations[currentLang][key]) {
            if (element.tagName === 'INPUT' || element.tagName === 'TEXTAREA') {
                element.placeholder = translations[currentLang][key];
            } else {
                element.innerText = translations[currentLang][key];
            }
        }
    });
}

document.addEventListener("DOMContentLoaded", applyTranslations);
