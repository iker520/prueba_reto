---
name: MouroSub Blueprint
colors:
  surface: '#f7f9fb'
  surface-dim: '#d8dadc'
  surface-bright: '#f7f9fb'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f2f4f6'
  surface-container: '#eceef0'
  surface-container-high: '#e6e8ea'
  surface-container-highest: '#e0e3e5'
  on-surface: '#191c1e'
  on-surface-variant: '#414750'
  inverse-surface: '#2d3133'
  inverse-on-surface: '#eff1f3'
  outline: '#727781'
  outline-variant: '#c1c7d2'
  surface-tint: '#1261a3'
  primary: '#004275'
  on-primary: '#ffffff'
  primary-container: '#005a9c'
  on-primary-container: '#afd1ff'
  inverse-primary: '#a1c9ff'
  secondary: '#505f76'
  on-secondary: '#ffffff'
  secondary-container: '#d0e1fb'
  on-secondary-container: '#54647a'
  tertiary: '#3b4248'
  on-tertiary: '#ffffff'
  tertiary-container: '#52595f'
  on-tertiary-container: '#c9cfd7'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d2e4ff'
  primary-fixed-dim: '#a1c9ff'
  on-primary-fixed: '#001c37'
  on-primary-fixed-variant: '#00487f'
  secondary-fixed: '#d3e4fe'
  secondary-fixed-dim: '#b7c8e1'
  on-secondary-fixed: '#0b1c30'
  on-secondary-fixed-variant: '#38485d'
  tertiary-fixed: '#dde3eb'
  tertiary-fixed-dim: '#c1c7cf'
  on-tertiary-fixed: '#161c22'
  on-tertiary-fixed-variant: '#41474e'
  background: '#f7f9fb'
  on-background: '#191c1e'
  surface-variant: '#e0e3e5'
typography:
  headline-xl:
    fontFamily: Inter
    fontSize: 40px
    fontWeight: '700'
    lineHeight: '1.2'
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: '1.25'
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.3'
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: '1.6'
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.6'
  label-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: '1.2'
  caption:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: '1.4'
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  unit: 8px
  container-max: 1280px
  gutter: 24px
  margin-mobile: 16px
  margin-desktop: 48px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 32px
---

## Brand & Style

The design system is engineered for a professional diving school, balancing the technical precision of underwater exploration with the clarity of a high-fidelity wireframe. The brand personality is authoritative yet welcoming, aiming to instill confidence in novice divers and respect among seasoned professionals. 

The chosen style is **High-Fidelity Wireframe**. This approach prioritizes structural integrity, content hierarchy, and navigation flow over decorative flourishes. It utilizes "Diving Blue" as a functional accent to guide the eye to key actions and data points, while the rest of the interface remains neutral and structural. This aesthetic ensures that the complex logistics of diving—schedules, gear lists, and certification levels—are presented with absolute clarity.

## Colors

The color palette is restricted to maintain the wireframe's structural focus. "Diving Blue" (#005A9C) serves as the primary action color, used exclusively for interactive elements, primary buttons, and critical status indicators. 

Light grays provide the necessary scaffolding for backgrounds and borders, while pure white is reserved for content containers to ensure maximum legibility. Gradients are avoided; color is applied in solid blocks to reinforce the technical, diagrammatic feel of the interface.

## Typography

The design system utilizes **Inter** for all typographic needs to ensure a utilitarian, systematic, and highly legible appearance. The hierarchy is strictly enforced through weight and size variations rather than color. 

Headlines use a tighter letter-spacing and heavier weights to anchor sections, while body text maintains a generous line height for readability during intensive information gathering. Labels and technical metadata use uppercase styling to differentiate them from narrative content.

## Layout & Spacing

This design system employs a **Fixed Grid** model for desktop and a fluid single-column model for mobile. The layout is built on an 8px base unit, ensuring all components and containers align to a consistent mathematical rhythm.

A standard 12-column grid is used for desktop layouts, with 24px gutters to provide ample breathing room between structural blocks. Vertical rhythm is maintained by using standardized stack units (8px, 16px, 32px) to define the relationship between related and unrelated content blocks.

## Elevation & Depth

To maintain the high-fidelity wireframe aesthetic, depth is communicated through **Low-contrast outlines** and **Tonal layers** rather than heavy shadows. 

- **Level 0 (Base):** The primary background color (#F8FAFC).
- **Level 1 (Cards/Containers):** Pure white backgrounds with a 1px solid border (#E2E8F0).
- **Level 2 (Hover/Active):** A very subtle, diffused shadow (0px 4px 12px rgba(0, 0, 0, 0.05)) is used only for interactive cards to indicate lift.
- **Overlays:** Modals and dropdowns use a 1px solid border in a darker gray (#CBD5E1) to distinguish them from the base layout.

## Shapes

The shape language is **Soft**, utilizing a consistent 0.25rem (4px) corner radius for most UI elements. This subtle rounding softens the technical nature of the wireframe without making it appear overly "consumer-grade" or playful. 

Buttons, input fields, and service cards all share this radius. Large structural elements, like hero sections or footer containers, may use a larger 0.5rem (8px) radius to define major content areas.

## Components

### Buttons
- **Primary:** Solid 'Diving Blue' background with white text. No gradients.
- **Secondary:** White background with a 1px 'Diving Blue' border and blue text.
- **Ghost:** No background or border; blue text only. Used for tertiary actions.

### Form Elements
- **Inputs:** 1px solid border (#E2E8F0) with a white background. On focus, the border changes to 'Diving Blue'.
- **Placeholders:** Text is set in a light gray (#94A3B8) to clearly distinguish from user input.

### Cards (Services/Instructors)
- **Structure:** White background, 1px border. 
- **Media:** Use an image placeholder (gray box with a centered icon) for service photos or instructor headshots.
- **Content:** Headline for the name, body text for the description, and a primary button for the call-to-action.

### Headers & Navigation
- **Top Bar:** Fixed position with a white background and a subtle bottom border.
- **Links:** Heavy weight labels with clear 'Diving Blue' underlines for active states.

### Dynamic Content Placeholders
- Use "Skeleton" loaders (shimmering light gray blocks) for data-heavy sections like dive logs or equipment availability to maintain the wireframe feel during state transitions.