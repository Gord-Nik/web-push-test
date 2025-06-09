self.addEventListener('push', event => {
    const { title = '(no title)', body = '' } = event.data?.json() ?? {};
    event.waitUntil(
        self.registration.showNotification(title, { body })
    );
});