import React, { useState } from 'react';
import './App.css';
import { initializeSimulation, allocatePage, performMemoryAccess } from './api/simulatorApi';

// Components
import StatsPanel from './components/StatsPanel';
import LogPanel from './components/LogPanel';
import ControlPanel from './components/ControlPanel';
import ConfigPanel from './components/ConfigPanel'; // Import the new panel
import TlbView from './components/TlbView';
import PhysicalMemoryView from './components/PhysicalMemoryView';
import VirtualMemoryView from './components/VirtualMemoryView';
import PageTableView from './components/PageTableView';

// --- CONFIGURATION CONSTANTS ---

const ALGORITHMS = [
  { value: 'FIFO', label: 'FIFO (First-In, First-Out)' },
  { value: 'LRU', label: 'LRU (Least Recently Used)' },
  { value: 'CLOCK', label: 'Clock (Second-Chance)' },
  { value: 'RANDOM', label: 'Random Eviction' },
];

const ADDRESS_WIDTHS = [
  { value: 10, label: '10-bit (1KB Space, 3 Hex Digits)' },
  { value: 12, label: '12-bit (4KB Space, 3 Hex Digits)' },
  { value: 14, label: '14-bit (16KB Space, 4 Hex Digits)' },
];

const PAGE_SIZES = [
  { value: 16, label: '16 Bytes' },
  { value: 32, label: '32 Bytes' },
  { value: 64, label: '64 Bytes' },
];

const PHYSICAL_MEMORY_SIZES = [
  { value: 128, label: '128 Bytes' },
  { value: 256, label: '256 Bytes' },
  { value: 512, label: '512 Bytes' },
  { value: 1024, label: '1024 Bytes (1KB)' },
];

// Combine Size + Associativity into logical pairs to prevent invalid math
const TLB_CONFIGS = [
  { label: 'Direct Mapped (8 Entries)', size: 8, associativity: 1 },
  { label: '2-Way Set Associative (16 Entries)', size: 16, associativity: 2 },
  { label: '4-Way Set Associative (16 Entries)', size: 16, associativity: 4 },
  { label: '4-Way Set Associative (32 Entries)', size: 32, associativity: 4 },
  { label: 'Fully Associative (8 Entries)', size: 8, associativity: 8 },
];

function App() {
  const [simulationState, setSimulationState] = useState(null);
  const [stage, setStage] = useState('setup'); 

  // --- STATE: Configuration ---
  // Default values set to "Standard" (12-bit, 16B Page, 256B RAM)
  const [config, setConfig] = useState({
    virtualAddressWidth: 12,
    pageSize: 16,
    physicalMemorySize: 256,
    tlbSize: 16,
    tlbAssociativity: 4,
    algorithm: 'FIFO'
  });
  
  const [derivedValues, setDerivedValues] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  // --- HANDLERS ---

  // Handle generic dropdown changes
  const handleConfigChange = (e) => {
    const { name, value } = e.target;
    setConfig(prev => ({
      ...prev,
      // If it's the algorithm, keep as string, otherwise parse to int
      [name]: name === 'algorithm' ? value : parseInt(value, 10)
    }));
    // Reset calculations if hardware params change (except algorithm)
    if (name !== 'algorithm') {
      setDerivedValues(null);
    }
  };

  // Special handler for the TLB combo dropdown
  const handleTlbChange = (e) => {
    const index = parseInt(e.target.value, 10);
    const selectedTlb = TLB_CONFIGS[index];
    setConfig(prev => ({
      ...prev,
      tlbSize: selectedTlb.size,
      tlbAssociativity: selectedTlb.associativity
    }));
    setDerivedValues(null);
  };

  const handleCalculate = (e) => {
    e.preventDefault();
    const offsetBits = Math.log2(config.pageSize);
    const vpnBits = config.virtualAddressWidth - offsetBits;
    
    // VALIDATION: Ensure Page Size isn't larger than Address Space
    if (vpnBits <= 0) {
      setError("Invalid Configuration: Page Size is too large for this Address Width.");
      return;
    }

    const totalPages = 2 ** vpnBits;
    const numPhysicalFrames = config.physicalMemorySize / config.pageSize;
    
    // VALIDATION: Ensure Physical Memory is at least 1 frame
    if (numPhysicalFrames < 1) {
      setError("Invalid Configuration: Physical Memory is smaller than a single Page.");
      return;
    }

    // Split VPN into L1 (PDI) and L2 (PTI) for the Two-Level Page Table
    const pdiBits = Math.floor(vpnBits / 2); 
    const ptiBits = vpnBits - pdiBits;

    setDerivedValues({
      offsetBits,
      vpnBits,
      totalPages,
      numPhysicalFrames,
      pdiBits,
      ptiBits,
    });
    setError(null);
    setStage('calculated');
  };

  const handleInitialize = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);
    try {
      const response = await initializeSimulation(config);
      setSimulationState(response.data);
      setStage('running'); 
    } catch (err) {
      setError('Backend error. Is Java running?');
      console.error(err);
    }
    setIsLoading(false);
  };
  
  const handleAllocatePage = async (vpn) => {
    try {
      const response = await allocatePage(vpn, false);
      setSimulationState(response.data);
    } catch (err) { console.error(err); }
  };
  
  const handleMemoryAccess = async (accessRequest) => {
    try {
      const response = await performMemoryAccess(accessRequest);
      setSimulationState(response.data);
    } catch (err) { console.error(err); }
  };

  // --- RENDER HELPERS ---

  const renderSetupWizard = () => (
    <div className="container mt-4" style={{ maxWidth: '800px' }}>
      <div className="card shadow">
        <div className="card-header bg-dark text-white">
          <h2 className="mb-0">Virtual Memory Simulator Setup</h2>
        </div>
        <div className="card-body">
          <form onSubmit={stage === 'setup' ? handleCalculate : handleInitialize}>
            
            <div className="alert alert-secondary">
              Configure your architecture below.
            </div>

            <div className="row g-3 mb-3">
              {/* Virtual Address Width */}
              <div className="col-md-6">
                <label className="form-label fw-bold">Virtual Address Width</label>
                <select 
                  className="form-select" 
                  name="virtualAddressWidth"
                  value={config.virtualAddressWidth}
                  onChange={handleConfigChange}
                  disabled={stage !== 'setup'}
                >
                  {ADDRESS_WIDTHS.map(opt => (
                    <option key={opt.value} value={opt.value}>{opt.label}</option>
                  ))}
                </select>
              </div>

              {/* Page Size */}
              <div className="col-md-6">
                <label className="form-label fw-bold">Page Size</label>
                <select 
                  className="form-select" 
                  name="pageSize"
                  value={config.pageSize}
                  onChange={handleConfigChange}
                  disabled={stage !== 'setup'}
                >
                  {PAGE_SIZES.map(opt => (
                    <option key={opt.value} value={opt.value}>{opt.label}</option>
                  ))}
                </select>
              </div>

              {/* Physical Memory */}
              <div className="col-md-6">
                <label className="form-label fw-bold">Physical Memory Size</label>
                <select 
                  className="form-select" 
                  name="physicalMemorySize"
                  value={config.physicalMemorySize}
                  onChange={handleConfigChange}
                  disabled={stage !== 'setup'}
                >
                  {PHYSICAL_MEMORY_SIZES.map(opt => (
                    <option key={opt.value} value={opt.value}>{opt.label}</option>
                  ))}
                </select>
              </div>

              {/* TLB Configuration */}
              <div className="col-md-6">
                <label className="form-label fw-bold">TLB Architecture</label>
                <select 
                  className="form-select" 
                  onChange={handleTlbChange}
                  disabled={stage !== 'setup'}
                  // Determine which index matches current state
                  value={TLB_CONFIGS.findIndex(t => t.size === config.tlbSize && t.associativity === config.tlbAssociativity)}
                >
                  {TLB_CONFIGS.map((opt, index) => (
                    <option key={index} value={index}>{opt.label}</option>
                  ))}
                </select>
              </div>

              {/* Algorithm - Can be changed even after calculation (before run) */}
              <div className="col-md-12">
                <label className="form-label fw-bold">Page Replacement Algorithm</label>
                <select 
                  className="form-select" 
                  name="algorithm"
                  value={config.algorithm}
                  onChange={handleConfigChange}
                  disabled={stage === 'running'} 
                >
                  {ALGORITHMS.map(opt => (
                    <option key={opt.value} value={opt.value}>{opt.label}</option>
                  ))}
                </select>
              </div>
            </div>

            {/* Success Summary after Calculation */}
            {derivedValues && (
              <div className="alert alert-success mt-4">
                <h5 className="alert-heading">Architecture Summary:</h5>
                <hr />
                <div className="row small">
                  <div className="col-6">
                    <ul className="mb-0">
                      <li><strong>Address Space:</strong> {config.virtualAddressWidth} bits</li>
                      <li><strong>Offset Bits:</strong> {derivedValues.offsetBits} bits</li>
                      <li><strong>VPN Bits:</strong> {derivedValues.vpnBits} bits</li>
                      <li><strong>Hierarchy:</strong> {derivedValues.pdiBits} L1 / {derivedValues.ptiBits} L2</li>
                    </ul>
                  </div>
                  <div className="col-6">
                    <ul className="mb-0">
                      <li><strong>Total Virtual Pages:</strong> {derivedValues.totalPages}</li>
                      <li><strong>Physical Frames:</strong> {derivedValues.numPhysicalFrames}</li>
                      <li><strong>TLB:</strong> {config.tlbSize} entries, {config.tlbAssociativity}-way</li>
                    </ul>
                  </div>
                </div>
              </div>
            )}

            {/* Buttons */}
            {stage === 'setup' && (
              <button type="submit" className="btn btn-info w-100 mt-3 btn-lg text-white">
                1. Calculate Architecture
              </button>
            )}

            {stage === 'calculated' && (
              <div className="d-grid gap-2">
                <button type="submit" className="btn btn-primary btn-lg mt-3" disabled={isLoading}>
                  {isLoading ? 'Generating System...' : '2. Start Simulation'}
                </button>
                <button type="button" className="btn btn-outline-secondary" onClick={() => { setStage('setup'); setDerivedValues(null); }}>
                  Back to Configuration
                </button>
              </div>
            )}
            
            {error && <div className="alert alert-danger mt-3">{error}</div>}
          </form>
        </div>
      </div>
    </div>
  );

  const renderSimulator = () => (
    <div className="container-fluid mt-3">
      <div className="row">
        {/* LEFT COLUMN: Controls & Info */}
        <div className="col-lg-3">
          
          {/* NEW: Config Panel to show active architecture */}
          <ConfigPanel config={config} derivedValues={derivedValues} />

          <h4>Controls</h4>
          <ControlPanel 
            onMemoryAccess={handleMemoryAccess} 
            offsetBits={derivedValues ? derivedValues.offsetBits : 4}
            addressWidth={config.virtualAddressWidth}
          />
          
          <hr />
          
          <h4>Info</h4>
          <StatsPanel stats={simulationState} />
          <LogPanel messages={simulationState.simulationMessages} />
        </div>

        {/* RIGHT COLUMN: Visualizations */}
        <div className="col-lg-9">
          <div className="row">
            <div className="col-xl-6 mb-3"><TlbView tlb={simulationState.tlb} /></div>
            <div className="col-xl-6 mb-3"><PhysicalMemoryView physicalMemory={simulationState.physicalMemory} /></div>
            <div className="col-xl-6 mb-3"><VirtualMemoryView disk={simulationState.disk} onAllocatePage={handleAllocatePage} /></div>
            <div className="col-xl-6 mb-3"><PageTableView pageTables={simulationState.pageTables} /></div>
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <div className="App">
      {stage === 'running' ? renderSimulator() : renderSetupWizard()}
    </div>
  );
}

export default App;