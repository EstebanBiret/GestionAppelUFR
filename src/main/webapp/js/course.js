const contextPath = window._contextPath || '';
let selectedId = null;

document.addEventListener('DOMContentLoaded', function () {
  if (window._autoSelectCourseId) {
    const row = document.querySelector(
      '#coursTable tbody tr[onclick*="selectCours(' + window._autoSelectCourseId + ',"]'
    );
    if (row) row.click();
  }
});

function selectCours(id, row) {
    document.querySelectorAll('#coursTable tbody tr').forEach(r => r.classList.remove('selected'));
    row.classList.add('selected');
    document.getElementById('crs_' + id).checked = true;
    selectedId = id;

    const courseName = row.dataset.courseName;
    const className  = row.dataset.className;
    document.getElementById('seancesPanelTitle').textContent =
        courseName + (className ? ' - ' + className : '');

    document.getElementById('btnCreateSeance').disabled = false;

    document.getElementById('seancesBody').innerHTML =
        '<div class="seances-placeholder"><div class="seances-placeholder-text">Chargement…</div></div>';

    fetch(contextPath + '/scolarite/seances?courseId=' + id)
        .then(r => r.json())
        .then(renderSeances)
        .catch(() => {
            document.getElementById('seancesBody').innerHTML =
                '<div class="seances-placeholder"><div class="seances-placeholder-text">Erreur lors du chargement.</div></div>';
        });
}

function renderSeances(sessions) {
    const body = document.getElementById('seancesBody');
    if (!sessions.length) {
        body.innerHTML = `
            <div class="seances-placeholder">
                <div class="seances-placeholder-icon">📅</div>
                <div class="seances-placeholder-text">Aucune séance pour ce cours.</div>
            </div>`;
        return;
    }
    let html = `
        <table class="classes-table" id="seancesTable">
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Créneau</th>
                    <th>Groupe</th>
                    <th>Enseignant</th>
                </tr>
            </thead>
            <tbody>`;
    sessions.forEach(s => {
        html += `<tr>
            <td>${s.date}</td>
            <td>${s.startTime} – ${s.endTime}</td>
            <td>${s.group}</td>
            <td>${s.teacher}</td>
        </tr>`;
    });
    html += `</tbody></table>`;
    body.innerHTML = html;
}

function goToSeanceForm() {
    if (selectedId) {
        window.location.href = contextPath + '/scolarite/seances/form?courseId=' + selectedId;
    }
}

function confirmCancel(e) {
    if (!confirm('Abandonner les modifications ? Les changements non enregistrés seront perdus.')) {
        e.preventDefault();
        return false;
    }
    return true;
}