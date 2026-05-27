/* eslint-disable */
import { render, screen, fireEvent } from '@testing-library/react';
import { Login } from './Login';
import { MemoryRouter, Route } from 'react-router-dom';
import { createMemoryHistory } from 'history';
import { Router } from 'react-router-dom';
import { vi } from 'vitest';

describe('Login component', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  test('renders Login component and handles login via button click', () => {
    const history = createMemoryHistory();
    history.push('/login');
    render(
      <Router history={history}>
        <Login />
      </Router>
    );

    const input = screen.getByPlaceholderText('Name');
    fireEvent.change(input, { target: { value: 'TestUser' } });

    const button = screen.getByText('Connect');
    fireEvent.click(button);

    expect(localStorage.getItem('chat-username')).toBe('TestUser');
    expect(history.location.pathname).toBe('/chat');
  });

  test('renders Login component and handles login via Enter key', () => {
    const history = createMemoryHistory();
    history.push('/login');
    render(
      <Router history={history}>
        <Login />
      </Router>
    );

    const input = screen.getByPlaceholderText('Name');
    fireEvent.change(input, { target: { value: 'TestUser2' } });

    fireEvent.keyUp(input, { key: 'Enter', code: 'Enter', charCode: 13 });

    expect(localStorage.getItem('chat-username')).toBe('TestUser2');
    expect(history.location.pathname).toBe('/chat');
  });

  test('handles non-Enter key press without logging in', () => {
    const history = createMemoryHistory();
    history.push('/login');
    render(
      <Router history={history}>
        <Login />
      </Router>
    );

    const input = screen.getByPlaceholderText('Name');
    fireEvent.change(input, { target: { value: 'TestUser3' } });

    fireEvent.keyUp(input, { key: 'a', code: 'KeyA', charCode: 65 });

    expect(localStorage.getItem('chat-username')).toBeNull();
    expect(history.location.pathname).toBe('/login');
  });
});
