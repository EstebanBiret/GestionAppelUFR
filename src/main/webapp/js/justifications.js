function showDates() {
    const block = document.getElementById('datesBlock');
    block.style.display = 'flex';
    document.getElementById('startDate').required = true;
    document.getElementById('endDate').required   = true;
  }

  function hideDates() {
    const block = document.getElementById('datesBlock');
    block.style.display = 'none';
    document.getElementById('startDate').required = false;
    document.getElementById('endDate').required   = false;
  }