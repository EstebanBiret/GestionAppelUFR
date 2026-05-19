const contextPath = window._contextPath || '';
let selectedId = null;

function selectGroupe(id, row) {
  document.querySelectorAll('#groupesTable tbody tr').forEach(r => r.classList.remove('selected'));
  row.classList.add('selected');
  document.getElementById('grp_' + id).checked = true;
  selectedId = id;
  document.getElementById('btnModifier').disabled = false;
}

function goEdit() {
  if (selectedId) window.location.href = contextPath + '/scolarite/groupes/form?id=' + selectedId;
}