function toggleDropdown() {
    document.getElementById('dropdownMenu').classList.toggle('open');
}

function previewPhoto(input) {
    if (input.files && input.files[0]) {
      const reader = new FileReader();
      reader.onload = e => document.getElementById('previewImg').src = e.target.result;
      reader.readAsDataURL(input.files[0]);
    }
}

document.addEventListener('click', function(e) {
    if (!e.target.closest('.user-dropdown')) {
        document.getElementById('dropdownMenu').classList.remove('open');
    }
});