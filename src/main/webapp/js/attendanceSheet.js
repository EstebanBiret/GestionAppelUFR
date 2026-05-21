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
    // On bloque le bouton pour éviter que le prof ne clique 2 fois par erreur
    let btn = document.querySelector('#confirmModal .btn-primary');
    btn.disabled = true;
    btn.textContent = "Signature en cours...";

    // On change l'action cachée et on soumet le formulaire
    document.getElementById('submitAction').value = 'sign';
    document.getElementById('attendanceForm').submit();
}