import Cookies from 'js-cookie';

const API_BASE = '/api';

function getCsrfHeader(method) {
    const isMutation = ['POST', 'PUT', 'DELETE', 'PATCH'].includes(method.toUpperCase());
    if (!isMutation) return {};
    const token = Cookies.get('XSRF-TOKEN');
    return token ? { 'X-XSRF-TOKEN': token } : {};
}

function getCommonHeaders(headers) {
    return {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Requested-With': 'XMLHttpRequest', // Хороший тон для API
        ...headers
    };
}

export async function apiClient(endpoint, { method = 'GET', body, headers = {}, ...config } = {}) {
    const finalHeaders = {
        ...getCommonHeaders(headers),
        ...getCsrfHeader(method)
    };
    return fetch(`${API_BASE}${endpoint}`, {
        method,
        headers: finalHeaders,
        credentials: 'include',
        body: body ? JSON.stringify(body) : undefined,
        ...config,
    });
}