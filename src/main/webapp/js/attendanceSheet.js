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

function saveDraft() {
    document.getElementById('submitAction').value = 'save';
    document.getElementById('attendanceForm').submit();
}

function openSignModal() {
    document.getElementById('confirmModal').classList.add('active');
}

function closeConfirmModal() {
    document.getElementById('confirmModal').classList.remove('active');
}

function confirmSign() {
    let btn = document.querySelector('#confirmModal .btn-primary');
    btn.disabled = true;
    btn.textContent = "Signature en cours...";

    document.getElementById('submitAction').value = 'sign';
    document.getElementById('attendanceForm').submit();
}