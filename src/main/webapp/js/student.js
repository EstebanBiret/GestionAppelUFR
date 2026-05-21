function toggleDropdown() {
    document.getElementById('dropdownMenu').classList.toggle('open');
}

document.addEventListener('click', function(e) {
    if (!e.target.closest('.user-dropdown')) {
        document.getElementById('dropdownMenu').classList.remove('open');
    }
});