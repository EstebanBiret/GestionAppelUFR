function openConfirmModal() {
    document.getElementById('confirmModal').classList.add('active');
}

function closeConfirmModal() {
    document.getElementById('confirmModal').classList.remove('active');
}

function submitAttendanceForm() {
    document.querySelector('#confirmModal .btn-primary').disabled = true;
    document.querySelector('#confirmModal .btn-primary').textContent = "Validation...";

    document.getElementById('attendanceForm').submit();
}