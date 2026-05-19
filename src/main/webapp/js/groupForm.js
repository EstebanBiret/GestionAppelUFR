async function loadStudents(classId, currentGroupId, preCheckedIds) {

  const searchInput = document.querySelector('.search-bar');
  if (searchInput) searchInput.value = '';

  const tbody = document.getElementById('studentsTbody');

  if (!classId) {
    tbody.innerHTML = `
      <tr id="selectHint">
        <td colspan="4" class="select-class-hint">
          Sélectionnez d'abord une classe pour voir ses étudiants.
        </td>
      </tr>`;
    return;
  }

  tbody.innerHTML = '<tr><td colspan="4" class="empty-students">Chargement…</td></tr>';

  const res  = await fetch(`${window._contextPath}/scolarite/groupes/students-by-class?classId=${classId}`);
  const list = await res.json();

  if (list.length === 0) {
    tbody.innerHTML = '<tr><td colspan="4" class="empty-students">Aucun étudiant dans cette classe.</td></tr>';
    return;
  }

  tbody.innerHTML = list.map(s => {
    const isFI    = s.role === 'ETUDIANT_FI';
    const checked = preCheckedIds.includes(String(s.id))
                 || (currentGroupId && s.groupId == currentGroupId);
    const groupLabel = s.groupName
      ? `<span class="pill" style="background:var(--gris);color:var(--txt-muted)">${s.groupName}</span>`
      : `<span style="color:var(--txt-muted);font-size:.78rem">Aucun groupe</span>`;
    const avatarSrc = s.picturePath
      ? `${window._contextPath}/images/users/${s.picturePath}`
      : `${window._contextPath}/images/users/default.jpg`;

    return `
      <tr class="${checked ? 'checked-row' : ''}" onclick="toggleRow(this)">
        <td><input type="checkbox" name="checkedStudents" value="${s.id}" ${checked ? 'checked' : ''}
                   onclick="event.stopPropagation()"></td>
        <td data-val="${s.lastName} ${s.firstName}">
          <div class="check-student-info">
            <div class="avatar"><img src="${avatarSrc}" alt=""></div>
            <div>
              <div class="check-student-name">${s.firstName} ${s.lastName}</div>
              <div class="check-student-email">${s.email}</div>
            </div>
          </div>
        </td>
        <td data-val="${isFI ? 'FI' : 'FA'}">
          <span class="pill ${isFI ? 'pill-fi' : 'pill-fa'}">${isFI ? 'FI' : 'FA'}</span>
        </td>
        <td data-val="${s.groupName || ''}">
          ${groupLabel}
        </td>
      </tr>`;
  }).join('');
}

function toggleRow(tr) {
  const cb = tr.querySelector('input[type=checkbox]');
  cb.checked = !cb.checked;
  tr.classList.toggle('checked-row', cb.checked);
}

function sortCheckTable(tableId, colIndex, th) {
  const tbody = document.querySelector('#' + tableId + ' tbody');
  const rows  = Array.from(tbody.querySelectorAll('tr[onclick]'));
  const dir   = th.dataset.dir === 'asc' ? 1 : -1;
  th.dataset.dir = dir === 1 ? 'desc' : 'asc';

  th.closest('thead').querySelectorAll('th').forEach(h => {
    h.classList.remove('sorted');
    const icon = h.querySelector('.sort-icon');
    if (icon) icon.textContent = '↕';
  });
  th.classList.add('sorted');
  th.querySelector('.sort-icon').textContent = dir === 1 ? '↑' : '↓';

  rows.sort((a, b) => {
    const aVal = (a.querySelectorAll('td')[colIndex]?.dataset.val || '').toLowerCase();
    const bVal = (b.querySelectorAll('td')[colIndex]?.dataset.val || '').toLowerCase();
    return aVal.localeCompare(bVal, 'fr') * dir;
  });

  rows.forEach(r => tbody.appendChild(r));
}

function confirmCancel(e) {
  if (!confirm('Abandonner les modifications ? Les changements non enregistrés seront perdus.')) {
    e.preventDefault();
    return false;
  }
  return true;
}

function filterCheckTable(tbodyId, query) {
  const q    = query.toLowerCase().trim();
  const rows = document.getElementById(tbodyId).querySelectorAll('tr[onclick]');
  rows.forEach(row => {
    const text = Array.from(row.querySelectorAll('td'))
      .map(td => (td.dataset.val || td.textContent || '').toLowerCase())
      .join(' ');
    row.style.display = text.includes(q) ? '' : 'none';
  });
}