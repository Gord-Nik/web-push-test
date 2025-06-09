const VAPID_PUBLIC = 'BPblscWtfIcjakPrRsHINaRYA6mIdNlIXJATdiDoZ-VO8G_T7kwOCXdsgopfJgg82rPiJfltwa--_6Q2SdmGziE';

const base64ToUint8 = base64 => {
    const pad      = '='.repeat((4 - base64.length % 4) % 4);
    const encoded  = (base64 + pad).replace(/-/g, '+').replace(/_/g, '/');
    return Uint8Array.from(atob(encoded), c => c.charCodeAt(0));
};

async function subscribeAndSend() {
    if (!('serviceWorker' in navigator)) {
        alert('Service Worker unsupported'); return;
    }

    const swReg = await navigator.serviceWorker.register('/sw.js');
    await navigator.serviceWorker.ready;

    const permission = await (Notification.permission === 'default'
        ? Notification.requestPermission()
        : Notification.permission);

    if (permission !== 'granted') {
        alert('Notification permission required'); return;
    }

    const subscription = await swReg.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: base64ToUint8(VAPID_PUBLIC),
    });

    await fetch('/api/push/subscribe/demo', {
        method : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body   : JSON.stringify(subscription),
    });

    await fetch('/api/push/notify/demo', {
        method : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body   : JSON.stringify({ title: 'Hello', body: 'It works!' }),
    });
}

document.getElementById('go').addEventListener('click', subscribeAndSend);
