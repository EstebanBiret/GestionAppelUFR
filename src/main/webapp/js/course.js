const contextPath = window._contextPath || '';
let selectedId = null;

function selectCours(id, row) {
  document.querySelectorAll('#coursTable tbody tr').forEach(r => r.classList.remove('selected'));
  row.classList.add('selected');
  document.getElementById('crs_' + id).checked = true;
  selectedId = id;
}

function confirmCancel(e) {
  if (!confirm('Abandonner les modifications ? Les changements non enregistrés seront perdus.')) {
    e.preventDefault();
    return false;
  }
  return true;
}