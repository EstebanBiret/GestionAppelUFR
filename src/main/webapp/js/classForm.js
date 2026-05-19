function toggleRow(tr) {
  const cb = tr.querySelector('input[type=checkbox]');
  cb.checked = !cb.checked;
  tr.classList.toggle('checked-row', cb.checked);
}

function confirmCancel(e) {
  if (!confirm('Abandonner les modifications ? Les changements non enregistrés seront perdus.')) {
    e.preventDefault();
    return false;
  }
  return true;
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
    const aVal = (a.querySelectorAll('td')[colIndex]?.dataset.val
               || a.querySelectorAll('td')[colIndex]?.textContent || '').trim().toLowerCase();
    const bVal = (b.querySelectorAll('td')[colIndex]?.dataset.val
               || b.querySelectorAll('td')[colIndex]?.textContent || '').trim().toLowerCase();
    return aVal.localeCompare(bVal, 'fr') * dir;
  });

  rows.forEach(r => tbody.appendChild(r));
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