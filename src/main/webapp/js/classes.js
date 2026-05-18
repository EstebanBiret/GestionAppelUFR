let selectedId = null;

function selectClass(id, row) {
  document.querySelectorAll('#classesTable tbody tr')
    .forEach(r => r.classList.remove('selected'));
  row.classList.add('selected');
  document.getElementById('cls_' + id).checked = true;
  selectedId = id;
  document.getElementById('btnModifier').disabled = false;
}

function goEdit() {
  if (selectedId) {
    window.location.href = contextPath + '/scolarite/classes/form?id=' + selectedId;
  }
}