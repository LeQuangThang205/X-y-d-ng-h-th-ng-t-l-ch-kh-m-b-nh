/**
 * Ví dụ gọi API backend từ frontend
 * Backend chạy tại: http://localhost:8080
 */

// --- Gọi API GET /api/hello ---
document.getElementById('btn-hello')?.addEventListener('click', async function () {
    const resultEl = document.getElementById('api-result');
    if (!resultEl) return;
    resultEl.textContent = 'Đang gọi API...';
    try {
        const response = await fetch('/api/hello');
        const data = await response.json();
        resultEl.textContent = JSON.stringify(data, null, 2);
    } catch (err) {
        resultEl.textContent = 'Lỗi: ' + err.message;
    }
});

// --- Upload ảnh (POST /api/images/upload) ---
document.getElementById('btn-upload')?.addEventListener('click', async function () {
    const fileInput = document.getElementById('file-input');
    const statusEl = document.getElementById('upload-status');
    const previewEl = document.getElementById('preview');
    if (!fileInput || !('files' in fileInput) || !fileInput.files.length) {
        if (statusEl) statusEl.textContent = 'Vui lòng chọn ảnh.';
        return;
    }
    const formData = new FormData();
    // @ts-ignore
    formData.append('file', fileInput.files[0]);
    if (statusEl) statusEl.textContent = 'Đang upload...';
    if (previewEl) previewEl.style.display = 'none';
    try {
        const response = await fetch('/api/images/upload', {
            method: 'POST',
            body: formData
        });
        const data = await response.json();
        if (response.ok) {
            if (statusEl) statusEl.textContent = 'Upload thành công: ' + data.filename;
            if (previewEl) { previewEl.src = data.url; previewEl.style.display = 'block'; }
        } else {
            if (statusEl) statusEl.textContent = 'Lỗi: ' + (data.message || response.statusText);
        }
    } catch (err) {
        if (statusEl) statusEl.textContent = 'Lỗi: ' + err.message;
    }
});
