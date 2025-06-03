# Test info

- Name: TC001 - Entry page shows 3 main cards with correct labels
- Location: D:\Github Projects\Iot_system_DXC\frontend\tests\entry-cards.spec.ts:3:5

# Error details

```
Error: Timed out 5000ms waiting for expect(locator).toHaveCount(expected)

Locator: locator('.dashboard-cards .card')
Expected: 3
Received: 0
Call log:
  - expect.toHaveCount with timeout 5000ms
  - waiting for locator('.dashboard-cards .card')
    9 × locator resolved to 0 elements
      - unexpected value "0"

    at D:\Github Projects\Iot_system_DXC\frontend\tests\entry-cards.spec.ts:8:23
```

# Page snapshot

```yaml
- main:
  - img "IoTelligence"
  - heading "IoTellig e nce" [level=1]
  - heading "Connecting Your World" [level=1]
  - paragraph: Smart Solutions for a Connected Future
  - paragraph: Experience the power of IoT with our intelligent platform. Monitor, control, and automate your devices seamlessly from anywhere in the world.
  - img "IoTelligence"
  - heading "Welcome to IoTelligence" [level=2]
  - paragraph: Sign in to your account
  - button "Google Sign in with Google":
    - img "Google"
    - text: Sign in with Google
  - text: or continue with email Email Address
  - textbox "Email Address"
  - text: Password
  - textbox "Password"
  - checkbox "Remember me"
  - text: Remember me
  - button "Forgot password?"
  - button "Sign In" [disabled]
  - text: Don't have an account?
  - link "Sign up":
    - /url: /sign-up
  - heading "IoTelligence" [level=4]
  - paragraph: Empowering the future through intelligent IoT solutions.
  - link "":
    - /url: "#"
  - link "":
    - /url: "#"
  - link "":
    - /url: "#"
  - link "":
    - /url: "#"
  - link "About Us":
    - /url: "#"
  - text: •
  - link "Features":
    - /url: "#"
  - text: •
  - link "Documentation":
    - /url: "#"
  - text: •
  - link "Contact":
    - /url: "#"
  - link "info@iotelligence.com":
    - /url: mailto:info@iotelligence.com
  - text: •
  - link "+1 (555) 123-4567":
    - /url: tel:+15551234567
  - text: • 123 IoT Street, Tech City
  - paragraph: © 2025 IoTelligence. All rights reserved.
```

# Test source

```ts
   1 | import { test, expect } from '@playwright/test';
   2 |
   3 | test('TC001 - Entry page shows 3 main cards with correct labels', async ({ page }) => {
   4 |   await page.goto('http://localhost:4200'); // Update if deployed
   5 |
   6 |   // Grab all cards by their shared class
   7 |   const cards = page.locator('.dashboard-cards .card');
>  8 |   await expect(cards).toHaveCount(3);
     |                       ^ Error: Timed out 5000ms waiting for expect(locator).toHaveCount(expected)
   9 |
  10 |   // Check each card’s label
  11 |   await expect(cards.nth(0).locator('h3')).toHaveText(/Traffic Monitoring/i);
  12 |   await expect(cards.nth(1).locator('h3')).toHaveText(/Street Light Management/i);
  13 |   await expect(cards.nth(2).locator('h3')).toHaveText(/Air Pollution Monitoring/i);
  14 | });
  15 |
```