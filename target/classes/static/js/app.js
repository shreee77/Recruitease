// RecruitEase – Main JS

document.addEventListener('DOMContentLoaded', function () {

  // ── Auto-dismiss alerts ──────────────────────────────────────
  document.querySelectorAll('.alert').forEach(function (alert) {
    setTimeout(function () {
      alert.style.transition = 'opacity 0.5s';
      alert.style.opacity = '0';
      setTimeout(() => alert.remove(), 500);
    }, 4000);
  });

  // ── Sidebar mobile toggle ────────────────────────────────────
  const toggleBtn = document.getElementById('sidebarToggle');
  const sidebar = document.querySelector('.sidebar');
  if (toggleBtn && sidebar) {
    toggleBtn.addEventListener('click', () => sidebar.classList.toggle('open'));
    document.addEventListener('click', (e) => {
      if (!sidebar.contains(e.target) && !toggleBtn.contains(e.target))
        sidebar.classList.remove('open');
    });
  }

  // ── Active nav highlight ─────────────────────────────────────
  const currentPath = window.location.pathname;
  document.querySelectorAll('.nav-item').forEach(item => {
    const href = item.getAttribute('href');
    if (href && currentPath.startsWith(href)) item.classList.add('active');
  });

  // ── Confirm delete dialogs ───────────────────────────────────
  document.querySelectorAll('[data-confirm]').forEach(btn => {
    btn.addEventListener('click', function (e) {
      if (!confirm(this.getAttribute('data-confirm'))) e.preventDefault();
    });
  });

  // ── Copy/Paste protection for .protected elements ────────────
  document.querySelectorAll('.protected').forEach(el => {
    el.addEventListener('copy',  e => e.preventDefault());
    el.addEventListener('cut',   e => e.preventDefault());
    el.addEventListener('contextmenu', e => e.preventDefault());
    el.addEventListener('keydown', e => {
      if ((e.ctrlKey || e.metaKey) && ['c','x','a'].includes(e.key.toLowerCase()))
        e.preventDefault();
    });
  });

  // ── Screenshot detection (basic) ────────────────────────────
  document.addEventListener('keyup', function (e) {
    if (e.key === 'PrintScreen') {
      navigator.clipboard.writeText('');
      alert('Screenshots are disabled on this page.');
    }
  });

  // ── Tooltip init (Bootstrap-compatible) ─────────────────────
  const tooltipEls = document.querySelectorAll('[data-tooltip]');
  tooltipEls.forEach(el => {
    el.setAttribute('title', el.getAttribute('data-tooltip'));
  });

  // ── Form validation highlight ────────────────────────────────
  document.querySelectorAll('form').forEach(form => {
    form.addEventListener('submit', function () {
      form.querySelectorAll('[required]').forEach(field => {
        if (!field.value.trim()) field.style.borderColor = 'var(--primary)';
        else field.style.borderColor = '';
      });
    });
  });

  // ── Chat scroll to bottom ────────────────────────────────────
  const chatMessages = document.querySelector('.chat-messages');
  if (chatMessages) chatMessages.scrollTop = chatMessages.scrollHeight;
});
