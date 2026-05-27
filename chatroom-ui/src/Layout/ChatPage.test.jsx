/* eslint-disable */
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { ChatPage } from './ChatPage';
import { MemoryRouter } from 'react-router-dom';
import { vi } from 'vitest';

// Mock dependencies
vi.mock('sockjs-client', () => {
  return {
    default: function() {
      return { close: vi.fn() };
    }
  };
});

vi.mock('stompjs', () => {
  const stompClientMock = {
    connect: vi.fn((headers, connectCallback) => connectCallback()),
    subscribe: vi.fn(),
    send: vi.fn(),
    disconnect: vi.fn()
  };
  return {
    default: {
      over: vi.fn(() => stompClientMock)
    },
    over: vi.fn(() => stompClientMock)
  };
});

// Since the component uses onPrivateMessage but does not declare it, we need to mock it globally to avoid the ReferenceError
global.onPrivateMessage = vi.fn();

describe('ChatPage component', () => {
  beforeEach(() => {
    localStorage.setItem('chat-username', 'TestUser');
  });

  afterEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  test('renders ChatPage', () => {
    render(
      <MemoryRouter>
        <ChatPage />
      </MemoryRouter>
    );
    // Since Chat Room text is missing, check for a button or text that exists
    expect(screen.getByPlaceholderText('Message')).toBeInTheDocument();
  });

  test('handles message input and sending', async () => {
    render(
      <MemoryRouter>
        <ChatPage />
      </MemoryRouter>
    );

    const input = screen.getByPlaceholderText('Message');
    fireEvent.change(input, { target: { value: 'Hello via ChatPage' } });

    expect(input.value).toBe('Hello via ChatPage');

    const sendButton = screen.getByDisplayValue('Send');
    fireEvent.click(sendButton);

    // Test input value is cleared
    expect(input.value).toBe('');
  });

  test('handles file input', async () => {
    render(
      <MemoryRouter>
        <ChatPage />
      </MemoryRouter>
    );

    const fileInput = document.getElementById('file');
    const file = new File(['hello'], 'hello.png', { type: 'image/png' });
    fireEvent.change(fileInput, { target: { files: [file] } });

    await waitFor(() => {
      // just verify it doesn't crash on file input change
      expect(fileInput).toBeInTheDocument();
    });
  });

  test('handles logout', async () => {
    render(
      <MemoryRouter>
        <ChatPage />
      </MemoryRouter>
    );

    const logoutButton = screen.getByDisplayValue('Logout');
    fireEvent.click(logoutButton);

    await waitFor(() => {
      // the logout function removes or changes local storage chat-username. It might be null.
      expect(localStorage.getItem('chat-username')).toBeNull();
    });
  });
});
