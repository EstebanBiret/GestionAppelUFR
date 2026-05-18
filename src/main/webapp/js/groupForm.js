async function loadStudents(classId, currentGroupId, preCheckedIds) {
  const tbody = document.getElementById('studentsTbody');
  const hint  = document.getElementById('selectHint');

  if (!classId) {
    tbody.innerHTML = `
      <tr id="selectHint">
        <td colspan="5" class="select-class-hint">
          Sélectionnez d'abord une classe pour voir ses étudiants.
        </td>
      </tr>`;
    return;
  }

  if (hint) hint.style.display = 'none';
  tbody.innerHTML = '<tr><td colspan="5" class="empty-students">Chargement…</td></tr>';

  const res  = await fetch(`${window._contextPath}/scolarite/groupes/students-by-class?classId=${classId}`);
  const list = await res.json();

  if (list.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" class="empty-students">Aucun étudiant dans cette classe.</td></tr>';
    return;
  }

  tbody.innerHTML = list.map(s => {
    const isFI    = s.role === 'ETUDIANT_FI';
    const checked = preCheckedIds.includes(String(s.id))
                 || (currentGroupId && s.groupId == currentGroupId);
    const groupLabel = s.groupName
      ? `<span class="pill" style="background:var(--gris);color:var(--txt-muted)">${s.groupName}</span>`
      : `<span style="color:var(--txt-muted);font-size:.78rem">Aucun groupe</span>`;
    const initials = s.firstName[0].toUpperCase() + s.lastName[0].toUpperCase();
    const avatarSrc = s.picturePath
      ? `${window._contextPath}/images/users/${s.picturePath}`
      : `${window._contextPath}/images/users/default.jpg`;
    return `
      <tr class="${checked ? 'checked-row' : ''}" onclick="toggleRow(this)">
        <td><input type="checkbox" name="checkedStudents" value="${s.id}" ${checked ? 'checked' : ''}
                   onclick="event.stopPropagation()"></td>
        <td>
          <div class="check-student-info">
            <div class="avatar"><img src="${avatarSrc}" alt="${initials}"></div>
            <div>
              <div class="check-student-name">${s.firstName} ${s.lastName}</div>
              <div class="check-student-email">${s.email}</div>
            </div>
          </div>
        </td>
        <td><span class="pill ${isFI ? 'pill-fi' : 'pill-fa'}">${isFI ? 'FI' : 'FA'}</span></td>
        <td>${groupLabel}</td>
      </tr>`;
  }).join('');
}

function toggleRow(tr) {
  const cb = tr.querySelector('input[type=checkbox]');
  cb.checked = !cb.checked;
  tr.classList.toggle('checked-row', cb.checked);
}

document.getElementById('checkAll')?.addEventListener('change', function () {
  document.querySelectorAll('#studentsTbody input[type=checkbox]').forEach(cb => {
    cb.checked = this.checked;
    cb.closest('tr').classList.toggle('checked-row', this.checked);
  });
});