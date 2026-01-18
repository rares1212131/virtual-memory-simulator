import React, { useState } from 'react';

function ControlPanel({ onMemoryAccess, offsetBits, addressWidth }) {
  const [address, setAddress] = useState('');
  const [value, setValue] = useState('');

  const isReadDisabled = value.trim() !== '';

  // Calculate how many Hex digits we need based on bit width
  // 12 bits / 4 = 3 chars. 14 bits / 4 = 3.5 -> 4 chars.
  const maxHexChars = Math.ceil(addressWidth / 4);
  const placeholderText = "X".repeat(maxHexChars);

  // --- HELPER: Live Address Decoder ---
  const getAddressPreview = () => {
    if (!address) return null;
    const val = parseInt(address, 16);
    if (isNaN(val)) return null;

    const vpn = val >> offsetBits;
    const offset = val & ((1 << offsetBits) - 1);

    return (
      <div className="alert alert-info py-1 mt-2 mb-0 small">
        <strong>Interpret:</strong> VPN {vpn} (0x{vpn.toString(16).toUpperCase()}) | Offset {offset} (0x{offset.toString(16).toUpperCase()})
      </div>
    );
  };

  const handleSubmit = (isWrite) => {
    const addressNum = parseInt(address, 16);
    if (isNaN(addressNum)) {
      alert(`Please enter a valid hexadecimal address (e.g., ${placeholderText}).`);
      return;
    }

    let valueNum = 0;
    if (isWrite) {
        if (value.trim() === '') {
            alert('Please enter a value to write.');
            return;
        }
        valueNum = parseInt(value, 16);
        if (isNaN(valueNum) || valueNum < 0 || valueNum > 255) {
            alert('Please enter a valid hex byte value (00-FF).');
            return;
        }
    }

    onMemoryAccess({ 
        virtualAddress: addressNum, 
        write: isWrite, 
        value: valueNum 
    });
  };

  return (
    <div className="card">
      <div className="card-header">Manual Memory Access</div>
      <div className="card-body">
        
        {/* Address Input */}
        <div className="input-group mb-1">
          <span className="input-group-text" style={{ width: '70px' }}>Addr 0x</span>
          <input
            type="text"
            className="form-control"
            maxLength={maxHexChars} 
            placeholder={placeholderText}
            value={address}
            onChange={(e) => setAddress(e.target.value.toUpperCase())}
          />
        </div>
        <div className="form-text mb-2">
          Max: {maxHexChars} digits ({addressWidth} bits)
        </div>

        {getAddressPreview()}

        {/* Value Input */}
        <div className="input-group mt-3 mb-3">
          <span className="input-group-text" style={{ width: '70px' }}>Val 0x</span>
          <input
            type="text"
            className="form-control"
            placeholder="AA"
            maxLength={2}
            value={value}
            onChange={(e) => setValue(e.target.value.toUpperCase())}
          />
        </div>

        {/* Buttons */}
        <div className="d-grid gap-2 d-md-flex justify-content-md-end">
          <button 
            className="btn btn-primary me-md-2" 
            onClick={() => handleSubmit(false)} 
            disabled={isReadDisabled}
            title={isReadDisabled ? "Clear 'Val' to Read" : "Read Memory"}
          >
            Read
          </button>
          
          <button 
            className="btn btn-warning" 
            onClick={() => handleSubmit(true)}
          >
            Write
          </button>
        </div>

      </div>
    </div>
  );
}

export default ControlPanel;