export async function GET(request, { params }) { return handleProxy(request, params); }
export async function POST(request, { params }) { return handleProxy(request, params); }
export async function PUT(request, { params }) { return handleProxy(request, params); }
export async function DELETE(request, { params }) { return handleProxy(request, params); }
export async function PATCH(request, { params }) { return handleProxy(request, params); }

const BACKEND_URL = process.env.BACKEND_URL;

async function handleProxy(request, params) {
    const { path } = await params;
    const searchParams = new URL(request.url).search;
    const targetUrl = `${BACKEND_URL}/api/${path.join('/')}${searchParams}`;
    const headers = new Headers(request.headers);
    headers.delete('host');
    try {
        const response = await fetch(targetUrl, {
            method: request.method,
            headers: headers,
            body: request.method !== 'GET' && request.method !== 'HEAD' ? await request.blob() : null,
            cache: 'no-store',
        });
        return new Response(response.body, {
            status: response.status,
            headers: response.headers,
        });
    } catch (error) {
        console.error('Proxy error:', error);
        return new Response(JSON.stringify({ error: 'Backend is unavailable' }), {
            status: 502,
            headers: { 'Content-Type': 'application/json' },
        });
    }
}