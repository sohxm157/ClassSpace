import { useEffect } from 'react';
import '../styles/toast.css';

function Toast({ message, type = 'success', onClose }) {
    useEffect(() => {
        const timer = setTimeout(() => {
            onClose();
        }, 3000);

        return () => clearTimeout(timer);
    }, [onClose]);

    return (
        <div className={`toast toast--${type}`}>
            <div className="toast-icon">
                {type === 'success' ? '✓' : '⚠️'}
            </div>
            <div className="toast-message">{message}</div>
            <button className="toast-close" onClick={onClose}>
                ✕
            </button>
        </div>
    );
}

export default Toast;
