import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api/simulation',
    headers: {
        'Content-Type': 'application/json',
    },
 
    withCredentials: true, // This tells axios to send cookies with requests
});
/**
 * Initializes the simulation with the given configuration.
 * @param {object} config - The configuration object.
 * @returns {Promise<object>} The initial simulation state.
 */
export const initializeSimulation = (config) => {
    return api.post('/initialize', config);
};

/**
 * Allocates a virtual page on disk.
 * @param {number} vpn - The virtual page number to allocate.
 * @param {boolean} writeProtected - If the page should be write-protected.
 * @returns {Promise<object>} The updated simulation state.
 */
export const allocatePage = (vpn, writeProtected) => {
    return api.post(`/allocate?vpn=${vpn}&writeProtected=${writeProtected}`);
};

/**
 * Performs a memory access (read or write).
 * @param {object} accessRequest - The memory access request object.
 * @returns {Promise<object>} The updated simulation state.
 */
export const performMemoryAccess = (accessRequest) => {
    return api.post('/access', accessRequest);
};