/**
 * Toast notification utility for CareSync AI
 */

const toastStyles = `
.toast-container {
    z-index: 9999;
}
.toast {
    background: white;
    border-radius: 12px;
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
    border: none;
    overflow: hidden;
}
.toast-header {
    background: transparent;
    border-bottom: none;
    padding: 12px 15px 5px;
}
.toast-body {
    padding: 10px 15px 15px;
    font-weight: 500;
}
.toast.success {
    border-left: 5px solid #10b981;
}
.toast.error {
    border-left: 5px solid #ef4444;
}
.toast.warning {
    border-left: 5px solid #f59e0b;
}
`;

// Inject styles
const styleSheet = document.createElement("style");
styleSheet.innerText = toastStyles;
document.head.appendChild(styleSheet);

function showToast(message, type = 'success') {
    // Create container if not exists
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
        document.body.appendChild(container);
    }

    const toastId = 'toast-' + Date.now();
    const icon = type === 'success' ? 'fa-check-circle' : (type === 'error' ? 'fa-exclamation-circle' : 'fa-info-circle');
    const colorClass = type === 'success' ? 'text-success' : (type === 'error' ? 'text-danger' : 'text-warning');
    const title = type.charAt(0).toUpperCase() + type.slice(1);

    const toastHtml = `
        <div id="${toastId}" class="toast hide ${type}" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header">
                <i class="fa-solid ${icon} ${colorClass} me-2"></i>
                <strong class="me-auto ${colorClass}">${title}</strong>
                <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body">
                ${message}
            </div>
        </div>
    `;

    container.insertAdjacentHTML('beforeend', toastHtml);
    const toastElement = document.getElementById(toastId);
    const bsToast = new bootstrap.Toast(toastElement, { delay: 5000 });
    bsToast.show();

    // Remove from DOM after hidden
    toastElement.addEventListener('hidden.bs.toast', () => {
        toastElement.remove();
    });
}
