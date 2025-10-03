import { Client } from '@stomp/stompjs';

const stompClient = new Client({
    brokerURL: '/api/ws',
    reconnectDelay: 5000,
    debug: console.debug,
    beforeConnect: (c) => {
        console.info('connecting to ' + c.brokerURL);
    },
    onConnect: () => {
        console.info('connected to ws');
    },
    onStompError: (frame) => {
        console.error('broker reported error: ' + frame.headers['message']);
        console.error('additional details: ' + frame.body)
    }
});

export { stompClient };
