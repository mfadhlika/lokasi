
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';


const socket = new SockJS('/api/ws');

const stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    connectHeaders: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
    },
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
