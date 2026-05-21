const ROLE_LABELS = window._roleLabels || {};
const CTX = window._contextPath || '';

async function saveRole(uid, btn) {
  const row     = btn.closest('tr');
  const select  = row.querySelector('.role-select');
  const newRole = select.value;

  btn.disabled = true;
  btn.textContent = '…';

  try {
    const params = new URLSearchParams();
    params.append('userId', uid);
    params.append('role', newRole);

    const res  = await fetch(CTX + '/admin/', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params.toString()
    });
    const data = await res.json();

    if (!data.success) { showToast('Erreur : ' + data.message, 'error'); return; }

    if (newRole === 'EN_ATTENTE') moveToPending(row, uid, newRole);
    else                       moveToAssigned(row, uid, newRole);

    showToast(`✓ Rôle mis à jour : ${ROLE_LABELS[newRole]}`, 'success');
    updateCounts();

  } catch (e) {
    showToast('Erreur réseau', 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Enregistrer';
  }
}

function moveToPending(row, uid, role) {
  const pendingTbody = document.getElementById('pendingTbody');
  document.getElementById('assignedTbody').removeChild(row);

  const cells = row.querySelectorAll('td');
  if (cells.length === 5) cells[2].remove();

  const select = row.querySelector('.role-select');
  let pendingOpt = select.querySelector('option[value="EN_ATTENTE"]');
  if (!pendingOpt) {
    pendingOpt = document.createElement('option');
    pendingOpt.value = 'EN_ATTENTE';
    pendingOpt.textContent = 'En attente';
    select.insertBefore(pendingOpt, select.firstChild);
  }
  select.value = 'EN_ATTENTE';

  const emptyRow = document.getElementById('pendingEmpty');
  if (emptyRow) emptyRow.remove();

  pendingTbody.appendChild(row);
}

function moveToAssigned(row, uid, role) {
  const assignedTbody = document.getElementById('assignedTbody');
  const pendingTbody  = document.getElementById('pendingTbody');

  if (pendingTbody.contains(row)) {
    pendingTbody.removeChild(row);

    const cells = row.querySelectorAll('td');
    const td = document.createElement('td');
    td.className = 'role-badge-cell';
    td.innerHTML = `<span class="role-badge role-${role}" id="badge_${uid}">${ROLE_LABELS[role]}</span>`;
    cells[1].after(td);

    const select = row.querySelector('.role-select');
    if (!select.querySelector('option[value="EN_ATTENTE"]')) {
      const opt = document.createElement('option');
      opt.value = 'EN_ATTENTE';
      opt.textContent = 'En attente';
      select.insertBefore(opt, select.firstChild);
    }
  } else {
    assignedTbody.removeChild(row);
    const badge = row.querySelector('.role-badge');
    if (badge) {
      badge.className = `role-badge role-${role}`;
      badge.textContent = ROLE_LABELS[role];
    }
  }

  const select = row.querySelector('.role-select');
  select.value = role;

  const emptyRow = document.getElementById('assignedEmpty');
  if (emptyRow) emptyRow.remove();

  assignedTbody.appendChild(row);
}

function updateCounts() {
  const pc = document.getElementById('pendingTbody').querySelectorAll('tr[data-uid]').length;
  const ac = document.getElementById('assignedTbody').querySelectorAll('tr[data-uid]').length;
  document.getElementById('pendingCount').textContent  = pc;
  document.getElementById('assignedCount').textContent = ac;
  document.getElementById('pendingLabel').textContent  = pc + ' utilisateur(s)';
  document.getElementById('assignedLabel').textContent = ac + ' utilisateur(s)';

  if (pc === 0 && !document.getElementById('pendingEmpty')) {
    const tr = document.createElement('tr');
    tr.id = 'pendingEmpty';
    tr.innerHTML = '<td colspan="4" class="empty-table">Aucun utilisateur en attente.</td>';
    document.getElementById('pendingTbody').appendChild(tr);
  }
}

function filterTable(tbodyId, query) {
  const q    = query.toLowerCase().trim();
  const rows = document.getElementById(tbodyId).querySelectorAll('tr[data-uid]');
  let visible = 0;
  rows.forEach(row => {
    const match = row.dataset.search.includes(q);
    row.style.display = match ? '' : 'none';
    if (match) visible++;
  });
  const labelId = tbodyId === 'pendingTbody' ? 'pendingLabel' : 'assignedLabel';
  document.getElementById(labelId).textContent = visible + ' utilisateur(s)';
}

function sortTable(tbodyId, colIndex, th) {
  const tbody = document.getElementById(tbodyId);
  const rows  = Array.from(tbody.querySelectorAll('tr[data-uid]'));
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
    const aVal = a.querySelectorAll('td')[colIndex]?.textContent.trim().toLowerCase() || '';
    const bVal = b.querySelectorAll('td')[colIndex]?.textContent.trim().toLowerCase() || '';
    return aVal.localeCompare(bVal, 'fr') * dir;
  });

  rows.forEach(r => tbody.appendChild(r));
}

let toastTimer;
function showToast(msg, type = 'success') {
  const t = document.getElementById('toast');
  t.textContent = msg;
  t.className = `toast ${type} show`;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => t.classList.remove('show'), 3500);
}